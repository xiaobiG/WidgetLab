package ext.android.widgets.composite.datepicker

data class MarkedDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val style: HighlightStyle = HighlightStyle.DOT
) {
    init {
        require(month in 1..12) { "month must be 1..12" }
        require(day in 1..31) { "day must be 1..31" }
    }

    fun toCalendarDate(): CalendarDate = CalendarDate(year, month, day)
}
