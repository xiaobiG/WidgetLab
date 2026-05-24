package ext.android.widgets.composite.datepicker

import android.graphics.Color

data class DatePickerTheme(
    val primaryColor: Int,
    val secondaryColor: Int,
    val textColor: Int,
    val highlightColor: Int
) {
    val panelBackgroundColor: Int = Color.WHITE
    val cancelTextColor: Int = ColorUtils.withAlpha(textColor, 0.6f)
    val onPrimaryColor: Int = ColorUtils.contrastOn(primaryColor)

    companion object {
        fun defaults(): DatePickerTheme = DatePickerTheme(
            primaryColor = Color.parseColor("#1976D2"),
            secondaryColor = Color.parseColor("#455A64"),
            textColor = Color.parseColor("#212121"),
            highlightColor = Color.parseColor("#FF9800")
        )
    }
}
