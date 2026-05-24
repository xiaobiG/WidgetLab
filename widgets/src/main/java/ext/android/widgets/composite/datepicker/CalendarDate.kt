package ext.android.widgets.composite.datepicker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Parcelable, Comparable<CalendarDate> {

    init {
        require(month in 1..12) { "month must be 1..12" }
        require(day in 1..31) { "day must be 1..31" }
    }

    override fun compareTo(other: CalendarDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    fun isValid(): Boolean = CalendarHelper.isValidDate(year, month, day)

    companion object {
        fun today(): CalendarDate = CalendarHelper.today()
    }
}
