package ext.android.widgets.composite.datepicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.min

internal class DayCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val backgroundRect = RectF()

    private var cellState: CellState = CellState.EMPTY
    private var theme: DatePickerTheme = DatePickerTheme.defaults()

    init {
        gravity = Gravity.CENTER
        includeFontPadding = false
        isClickable = true
        isFocusable = true
    }

    fun bindTheme(theme: DatePickerTheme) {
        this.theme = theme
        applyState()
        invalidate()
    }

    fun bindState(state: CellState) {
        if (cellState == state) return
        cellState = state
        applyState()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (cellState == CellState.EMPTY) return

        val radius = dp(8f)
        val cx = width / 2f
        val cy = height / 2f
        val half = min(width, height) / 2f - dp(2f)

        when {
            cellState.selected -> {
                backgroundRect.set(cx - half, cy - half, cx + half, cy + half)
                highlightPaint.color = theme.primaryColor
                canvas.drawRoundRect(backgroundRect, radius, radius, highlightPaint)
            }
            cellState.markedStyle == HighlightStyle.BACKGROUND -> {
                backgroundRect.set(cx - half, cy - half, cx + half, cy + half)
                highlightPaint.color = theme.highlightColor
                canvas.drawRoundRect(backgroundRect, radius, radius, highlightPaint)
            }
        }

        super.onDraw(canvas)

        if (cellState.markedStyle == HighlightStyle.DOT && cellState.day > 0) {
            val dotColor = when {
                cellState.selected -> theme.onPrimaryColor
                else -> theme.highlightColor
            }
            dotPaint.color = dotColor
            val dotRadius = dp(2f)
            canvas.drawCircle(cx, height - dp(8f), dotRadius, dotPaint)
        }
    }

    private fun applyState() {
        when {
            cellState == CellState.EMPTY -> {
                text = ""
                visibility = INVISIBLE
                isEnabled = false
            }
            cellState.selected -> {
                text = cellState.day.toString()
                visibility = VISIBLE
                isEnabled = true
                setTextColor(theme.onPrimaryColor)
            }
            else -> {
                text = cellState.day.toString()
                visibility = VISIBLE
                isEnabled = true
                setTextColor(theme.textColor)
            }
        }
    }

    data class CellState(
        val day: Int = 0,
        val selected: Boolean = false,
        val markedStyle: HighlightStyle? = null
    ) {
        companion object {
            val EMPTY = CellState()
        }
    }

    private fun dp(value: Float): Float =
        value * resources.displayMetrics.density
}
