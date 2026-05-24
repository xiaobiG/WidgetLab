package ext.android.widgets.composite.datepicker

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import ext.android.widgets.R
import ext.android.widgets.databinding.WidgetDatePickerPanelBinding
import kotlinx.parcelize.Parcelize

class DatePickerPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OnPanelActionListener {
        fun onDateSelected(date: CalendarDate)
        fun onConfirmClicked(selectedDate: CalendarDate?)
        fun onCancelClicked()
    }

    private val binding: WidgetDatePickerPanelBinding
    private val dayCells = Array(GRID_CELL_COUNT) { index ->
        DayCellView(context).also { cell ->
            cell.layoutParams = GridLayout.LayoutParams().apply {
                width = dpToPx(DAY_CELL_DP)
                height = dpToPx(DAY_CELL_DP)
                columnSpec = GridLayout.spec(index % 7, 1f)
                rowSpec = GridLayout.spec(index / 7)
            }
        }
    }

    private var theme: DatePickerTheme = DatePickerTheme.defaults()
    private var displayYear: Int = 0
    private var displayMonth: Int = 0
    private var selectedDate: CalendarDate? = null
    private var markedDates: Map<CalendarDate, HighlightStyle> = emptyMap()
    private var panelActionListener: OnPanelActionListener? = null

    init {
        binding = WidgetDatePickerPanelBinding.inflate(android.view.LayoutInflater.from(context), this, true)
        context.obtainStyledAttributes(attrs, R.styleable.DatePickerPanelView, defStyleAttr, 0).apply {
            try {
                if (hasValue(R.styleable.DatePickerPanelView_datePickerPrimaryColor)) {
                    theme = DatePickerTheme(
                        primaryColor = getColor(
                            R.styleable.DatePickerPanelView_datePickerPrimaryColor,
                            theme.primaryColor
                        ),
                        secondaryColor = getColor(
                            R.styleable.DatePickerPanelView_datePickerSecondaryColor,
                            theme.secondaryColor
                        ),
                        textColor = getColor(
                            R.styleable.DatePickerPanelView_datePickerTextColor,
                            theme.textColor
                        ),
                        highlightColor = getColor(
                            R.styleable.DatePickerPanelView_datePickerHighlightColor,
                            theme.highlightColor
                        )
                    )
                }
            } finally {
                recycle()
            }
        }
        setupWeekHeader()
        setupDayGrid()
        setupActions()
        applyTheme()
    }

    fun setOnPanelActionListener(listener: OnPanelActionListener?) {
        panelActionListener = listener
    }

    fun configure(config: DatePickerConfig) {
        theme = config.theme
        markedDates = config.markedDates
        selectedDate = config.initialSelectedDate
        displayYear = config.displayYear
        displayMonth = config.displayMonth
        binding.datePickerTitle.text = config.title
        applyTheme()
        refreshMonthGrid()
        updateConfirmEnabled()
    }

    fun applyTheme(theme: DatePickerTheme) {
        this.theme = theme
        applyTheme()
        refreshMonthGrid()
    }

    fun getSelectedDate(): CalendarDate? = selectedDate

    fun getDisplayYear(): Int = displayYear

    fun getDisplayMonth(): Int = displayMonth

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(
            superSavedState = superState,
            displayYear = displayYear,
            displayMonth = displayMonth,
            selectedDate = selectedDate
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superSavedState)
            displayYear = state.displayYear
            displayMonth = state.displayMonth
            selectedDate = state.selectedDate
            refreshMonthGrid()
            updateConfirmEnabled()
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun setupWeekHeader() {
        val labels = listOf(
            R.string.date_picker_week_mon,
            R.string.date_picker_week_tue,
            R.string.date_picker_week_wed,
            R.string.date_picker_week_thu,
            R.string.date_picker_week_fri,
            R.string.date_picker_week_sat,
            R.string.date_picker_week_sun
        )
        labels.forEach { labelRes ->
            val label = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
                setText(labelRes)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
            binding.datePickerWeekHeader.addView(label)
        }
    }

    private fun setupDayGrid() {
        dayCells.forEach { binding.datePickerDayGrid.addView(it) }
        dayCells.forEachIndexed { index, cell ->
            cell.setOnClickListener {
                val day = cell.tag as? Int ?: return@setOnClickListener
                if (day <= 0) return@setOnClickListener
                val date = CalendarDate(displayYear, displayMonth, day)
                selectedDate = date
                refreshMonthGrid()
                updateConfirmEnabled()
                panelActionListener?.onDateSelected(date)
            }
            cell.tag = index
        }
    }

    private fun setupActions() {
        binding.datePickerPrevMonth.setOnClickListener {
            val (year, month) = CalendarHelper.addMonths(displayYear, displayMonth, -1)
            displayYear = year
            displayMonth = month
            refreshMonthGrid()
        }
        binding.datePickerNextMonth.setOnClickListener {
            val (year, month) = CalendarHelper.addMonths(displayYear, displayMonth, 1)
            displayYear = year
            displayMonth = month
            refreshMonthGrid()
        }
        binding.datePickerCancel.setOnClickListener {
            panelActionListener?.onCancelClicked()
        }
        binding.datePickerConfirm.setOnClickListener {
            panelActionListener?.onConfirmClicked(selectedDate)
        }
    }

    private fun applyTheme() {
        binding.root.setBackgroundResource(R.drawable.bg_date_picker_dialog)
        binding.datePickerTitle.setTextColor(theme.textColor)
        binding.datePickerMonthLabel.setTextColor(theme.secondaryColor)
        binding.datePickerCancel.setTextColor(theme.cancelTextColor)
        applyConfirmButtonBackground()

        tintNavButton(binding.datePickerPrevMonth)
        tintNavButton(binding.datePickerNextMonth)

        for (i in 0 until binding.datePickerWeekHeader.childCount) {
            (binding.datePickerWeekHeader.getChildAt(i) as? TextView)?.setTextColor(theme.secondaryColor)
        }
        dayCells.forEach { it.bindTheme(theme) }
    }

    private fun applyConfirmButtonBackground() {
        val radius = dpToPx(CONFIRM_BUTTON_HEIGHT_DP) / 2f
        binding.datePickerConfirm.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(theme.primaryColor)
        }
        binding.datePickerConfirm.backgroundTintList = null
        binding.datePickerConfirm.setTextColor(theme.onPrimaryColor)
    }

    private fun tintNavButton(button: ImageButton) {
        button.drawable?.let { drawable ->
            DrawableCompat.setTint(drawable.mutate(), theme.secondaryColor)
            button.setImageDrawable(drawable)
        }
    }

    private fun refreshMonthGrid() {
        binding.datePickerMonthLabel.text =
            CalendarHelper.formatMonthLabel(displayYear, displayMonth)

        val leading = CalendarHelper.leadingEmptyCells(displayYear, displayMonth)
        val daysInMonth = CalendarHelper.daysInMonth(displayYear, displayMonth)

        dayCells.forEachIndexed { index, cell ->
            val dayNumber = index - leading + 1
            if (dayNumber in 1..daysInMonth) {
                val date = CalendarDate(displayYear, displayMonth, dayNumber)
                val isSelected = selectedDate == date
                val markedStyle = markedDates[date]
                cell.tag = dayNumber
                cell.bindState(
                    DayCellView.CellState(
                        day = dayNumber,
                        selected = isSelected,
                        markedStyle = markedStyle
                    )
                )
            } else {
                cell.tag = -1
                cell.bindState(DayCellView.CellState.EMPTY)
            }
        }
    }

    private fun updateConfirmEnabled() {
        binding.datePickerConfirm.isEnabled = selectedDate != null
        binding.datePickerConfirm.alpha = if (selectedDate != null) 1f else 0.5f
    }

    private fun dpToPx(dp: Float): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()

    @Parcelize
    private class SavedState(
        val superSavedState: Parcelable?,
        val displayYear: Int,
        val displayMonth: Int,
        val selectedDate: CalendarDate?
    ) : View.BaseSavedState(superSavedState), Parcelable

    companion object {
        private const val GRID_CELL_COUNT = 42
        private const val DAY_CELL_DP = 40f
        private const val CONFIRM_BUTTON_HEIGHT_DP = 40f
    }
}
