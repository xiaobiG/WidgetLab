package ext.android.widgets.composite.datepicker

data class DatePickerConfig(
    val title: String,
    val theme: DatePickerTheme,
    val initialSelectedDate: CalendarDate?,
    val markedDates: Map<CalendarDate, HighlightStyle>,
    val displayYear: Int,
    val displayMonth: Int
)
