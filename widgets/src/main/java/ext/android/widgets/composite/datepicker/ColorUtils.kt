package ext.android.widgets.composite.datepicker

import android.graphics.Color

internal object ColorUtils {

    fun withAlpha(color: Int, alphaFraction: Float): Int {
        val alpha = (255f * alphaFraction.coerceIn(0f, 1f)).toInt()
        return Color.argb(
            alpha,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    fun contrastOn(backgroundColor: Int): Int {
        val luminance = (0.299 * Color.red(backgroundColor) +
            0.587 * Color.green(backgroundColor) +
            0.114 * Color.blue(backgroundColor)) / 255.0
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }
}
