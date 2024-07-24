package com.example.mobilemonitoringbankbpr

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide

class AppLifecycleObserver(private val context: Context) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // App is in the background
        clearCache()
    }

    private fun clearCache() {
        Glide.get(context).clearMemory()
        Thread {
            Glide.get(context).clearDiskCache()
        }.start()
    }
}
