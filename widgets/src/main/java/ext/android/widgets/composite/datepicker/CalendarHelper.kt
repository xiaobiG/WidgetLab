package ext.android.widgets.composite.datepicker

import java.util.Calendar

object CalendarHelper {

    fun today(): CalendarDate {
        val calendar = Calendar.getInstance()
        return CalendarDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        if (month !in 1..12 || day !in 1..31) return false
        val calendar = Calendar.getInstance().apply {
            isLenient = false
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return try {
            calendar.time
            calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month - 1 &&
                calendar.get(Calendar.DAY_OF_MONTH) == day
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    fun sanitizeDate(year: Int, month: Int, day: Int): CalendarDate? {
        return if (isValidDate(year, month, day)) CalendarDate(year, month, day) else null
    }

    fun daysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun leadingEmptyCells(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return (dayOfWeek + 5) % 7
    }

    fun addMonths(year: Int, month: Int, delta: Int): Pair<Int, Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, delta)
        }
        return calendar.get(Calendar.YEAR) to (calendar.get(Calendar.MONTH) + 1)
    }

    fun formatMonthLabel(year: Int, month: Int): String = "${year}年${month}月"
}
