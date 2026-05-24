package ext.android.widgets.composite.datepicker

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import ext.android.widgets.databinding.WidgetDatePickerPreviewBinding

class DatePickerPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetDatePickerPreviewBinding
    private var dialogConfigProvider: (() -> DatePickerDialog.Builder)? = null
    private var selectedDate: CalendarDate? = null

    init {
        binding = WidgetDatePickerPreviewBinding.inflate(
            android.view.LayoutInflater.from(context),
            this,
            true
        )
        binding.datePickerOpenButton.setOnClickListener { openPicker() }
        updateResultText()
    }

    fun setDialogConfigProvider(provider: () -> DatePickerDialog.Builder) {
        dialogConfigProvider = provider
    }

    fun getSelectedDate(): CalendarDate? = selectedDate

    fun updateDialogConfigProvider(provider: () -> DatePickerDialog.Builder) {
        dialogConfigProvider = provider
    }

    private fun openPicker() {
        val provider = dialogConfigProvider ?: return
        provider()
            .setOnDateSelectedListener { year, month, day ->
                selectedDate = CalendarDate(year, month, day)
                updateResultText()
            }
            .show()
    }

    private fun updateResultText() {
        val date = selectedDate
        binding.datePickerResult.text = if (date == null) {
            context.getString(ext.android.widgets.R.string.date_picker_no_selection)
        } else {
            context.getString(
                ext.android.widgets.R.string.date_picker_selected_format,
                date.year,
                date.month,
                date.day
            )
        }
    }
}
