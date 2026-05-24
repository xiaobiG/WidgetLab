package ext.android.widgets.composite.datepicker

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import ext.android.widgets.R

class DatePickerDialog private constructor(
    private val hostActivity: Activity,
    private val dialog: AlertDialog,
    private val panelView: DatePickerPanelView,
    private val onDateSelectedListener: ((Int, Int, Int) -> Unit)?,
    private val onCancelListener: (() -> Unit)?
) {

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    class Builder(private val context: Context) {
        internal var title: String = ""
        internal var theme: DatePickerTheme? = null
        internal var initialDate: CalendarDate? = null
        internal var defaultSelectToday: Boolean = true
        internal var clearInitialSelection: Boolean = false
        internal var markedDates: Set<MarkedDate> = emptySet()
        private var onDateSelectedListener: ((Int, Int, Int) -> Unit)? = null
        private var onCancelListener: (() -> Unit)? = null

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setTheme(theme: DatePickerTheme): Builder {
            this.theme = theme
            return this
        }

        fun setInitialDate(year: Int, month: Int, day: Int): Builder {
            initialDate = CalendarHelper.sanitizeDate(year, month, day)
            clearInitialSelection = false
            return this
        }

        fun clearInitialSelection(): Builder {
            clearInitialSelection = true
            initialDate = null
            defaultSelectToday = false
            return this
        }

        fun setMarkedDates(dates: Set<MarkedDate>): Builder {
            markedDates = dates
            return this
        }

        fun setOnDateSelectedListener(listener: (year: Int, month: Int, day: Int) -> Unit): Builder {
            onDateSelectedListener = listener
            return this
        }

        fun setOnCancelListener(listener: () -> Unit): Builder {
            onCancelListener = listener
            return this
        }

        fun show(): DatePickerDialog {
            val activity = findActivity(context)
                ?: throw IllegalArgumentException("DatePickerDialog requires an Activity context")

            val resolvedTheme = theme ?: DatePickerTheme.defaults()
            val markedMap = buildMarkedDateMap(markedDates)
            val sanitizedInitial = initialDate?.takeIf { it.isValid() }
                ?: initialDateFromBuilder()

            val displayAnchor = when {
                sanitizedInitial != null -> sanitizedInitial
                markedMap.isNotEmpty() -> markedMap.keys.min()
                else -> CalendarHelper.today()
            }

            val config = DatePickerConfig(
                title = title.ifEmpty { activity.getString(R.string.date_picker_open) },
                theme = resolvedTheme,
                initialSelectedDate = sanitizedInitial,
                markedDates = markedMap,
                displayYear = displayAnchor.year,
                displayMonth = displayAnchor.month
            )

            val panelView = DatePickerPanelView(activity)
            panelView.configure(config)

            var userConfirmed = false
            val dialog = AlertDialog.Builder(activity)
                .setView(panelView)
                .setOnCancelListener {
                    if (!userConfirmed) {
                        onCancelListener?.invoke()
                    }
                }
                .create()

            dialog.setCanceledOnTouchOutside(true)

            panelView.setOnPanelActionListener(object : DatePickerPanelView.OnPanelActionListener {
                override fun onDateSelected(date: CalendarDate) = Unit

                override fun onConfirmClicked(selectedDate: CalendarDate?) {
                    val date = selectedDate ?: return
                    userConfirmed = true
                    onDateSelectedListener?.invoke(date.year, date.month, date.day)
                    dialog.dismiss()
                }

                override fun onCancelClicked() {
                    dialog.cancel()
                }
            })

            val pickerDialog = DatePickerDialog(
                hostActivity = activity,
                dialog = dialog,
                panelView = panelView,
                onDateSelectedListener = onDateSelectedListener,
                onCancelListener = onCancelListener
            )
            pickerDialog.showDialog()
            return pickerDialog
        }

        private fun initialDateFromBuilder(): CalendarDate? {
            if (clearInitialSelection) return null
            if (defaultSelectToday) return CalendarHelper.today()
            return null
        }

        private fun buildMarkedDateMap(dates: Set<MarkedDate>): Map<CalendarDate, HighlightStyle> {
            val map = linkedMapOf<CalendarDate, HighlightStyle>()
            dates.forEach { marked ->
                CalendarHelper.sanitizeDate(marked.year, marked.month, marked.day)?.let { date ->
                    map[date] = marked.style
                }
            }
            return map
        }

        private fun findActivity(context: Context): Activity? {
            var current: Context? = context
            while (current is ContextWrapper) {
                if (current is Activity) return current
                current = current.baseContext
            }
            return null
        }
    }

    private fun showDialog() {
        dialog.show()
        applyDialogWindow()
        registerLifecycleCleanup()
    }

    private fun applyDialogWindow() {
        val window = dialog.window ?: return
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val width = minOf(
            (hostActivity.resources.displayMetrics.widthPixels - dpToPx(hostActivity, 48f)).toInt(),
            dpToPx(hostActivity, 320f).toInt()
        )
        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun registerLifecycleCleanup() {
        val app = hostActivity.application
        val callbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityDestroyed(activity: Activity) {
                if (activity === hostActivity) {
                    dismiss()
                    app.unregisterActivityLifecycleCallbacks(this)
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) = Unit
        }
        app.registerActivityLifecycleCallbacks(callbacks)
        dialog.setOnDismissListener {
            app.unregisterActivityLifecycleCallbacks(callbacks)
        }
    }

    private fun dpToPx(context: Context, dp: Float): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
}
