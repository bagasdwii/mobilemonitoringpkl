package com.example.mobilemonitoringbankbpr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter(private val onFilterSelected: (String) -> Unit) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val filters = listOf("Jakarta", "Bandung", "Surabaya")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]
        holder.bind(filter)
    }

    override fun getItemCount(): Int = filters.size

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(filter: String) {
            (itemView as TextView).text = filter
            itemView.setOnClickListener {
                onFilterSelected(filter)
            }
        }
    }
}
