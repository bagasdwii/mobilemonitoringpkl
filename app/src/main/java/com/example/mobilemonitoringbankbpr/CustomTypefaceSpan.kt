//import android.graphics.Typeface
//import android.text.TextPaint
import android.text.style.TypefaceSpan

//class CustomTypefaceSpan(family: String, private val newType: Typeface?) : TypefaceSpan(family) {
//    override fun updateDrawState(textPaint: TextPaint) {
//        applyCustomTypeFace(textPaint, newType)
//    }
//
//    override fun updateMeasureState(p: TextPaint) {
//        applyCustomTypeFace(p, newType)
//    }
//
//    private fun applyCustomTypeFace(paint: TextPaint, tf: Typeface?) {
//        val oldStyle: Int
//        val old = paint.typeface
//        oldStyle = old?.style ?: 0
//
//        val fake = oldStyle and tf!!.style.inv()
//        if (fake and Typeface.BOLD != 0) {
//            paint.isFakeBoldText = true
//        }
//
//        if (fake and Typeface.ITALIC != 0) {
//            paint.textSkewX = -0.25f
//        }
//
//        paint.typeface = tf
//    }
//}
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(family: String, private val typeface: Typeface) : MetricAffectingSpan() {

    override fun updateDrawState(p: TextPaint) {
        applyCustomTypeFace(p, typeface)
    }

    override fun updateMeasureState(p: TextPaint) {
        applyCustomTypeFace(p, typeface)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val old = paint.typeface
        val oldStyle: Int

        oldStyle = old?.style ?: 0

        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        paint.typeface = tf
    }
}
