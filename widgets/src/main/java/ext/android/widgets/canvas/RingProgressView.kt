package ext.android.widgets.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import ext.android.widgets.R
import kotlin.math.min

class RingProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val arcBounds = RectF()

    private var progress: Float = 0.35f
    private var strokeWidthPx: Float = dp(10f)
    private var ringColor: Int = 0xFF1976D2.toInt()
    private var trackColor: Int = 0xFFE0E0E0.toInt()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RingProgressView, defStyleAttr, 0).apply {
            try {
                progress = getFloat(R.styleable.RingProgressView_ringProgress, progress)
                strokeWidthPx = getDimension(R.styleable.RingProgressView_ringStrokeWidth, strokeWidthPx)
                ringColor = getColor(R.styleable.RingProgressView_ringColor, ringColor)
                trackColor = getColor(R.styleable.RingProgressView_ringTrackColor, trackColor)
            } finally {
                recycle()
            }
        }
        applyPaintColors()
    }

    fun setProgress(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        if (progress != clamped) {
            progress = clamped
            invalidate()
        }
    }

    fun getProgress(): Float = progress

    fun setRingStrokeWidth(widthPx: Float) {
        if (strokeWidthPx != widthPx) {
            strokeWidthPx = widthPx
            trackPaint.strokeWidth = strokeWidthPx
            progressPaint.strokeWidth = strokeWidthPx
            requestLayout()
            invalidate()
        }
    }

    fun getRingStrokeWidth(): Float = strokeWidthPx

    fun setRingColor(color: Int) {
        if (ringColor != color) {
            ringColor = color
            progressPaint.color = ringColor
            invalidate()
        }
    }

    fun getRingColor(): Int = ringColor

    fun setTrackColor(color: Int) {
        if (trackColor != color) {
            trackColor = color
            trackPaint.color = trackColor
            invalidate()
        }
    }

    fun getTrackColor(): Int = trackColor

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (strokeWidthPx * 2 + dp(120f)).toInt()
        val width = resolveSize(desired, widthMeasureSpec)
        val height = resolveSize(desired, heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val inset = strokeWidthPx / 2f + paddingLeft
        arcBounds.set(
            inset,
            inset,
            width - inset - paddingRight,
            height - inset - paddingBottom
        )
        canvas.drawArc(arcBounds, 0f, 360f, false, trackPaint)
        canvas.drawArc(arcBounds, -90f, progress * 360f, false, progressPaint)
    }

    private fun applyPaintColors() {
        trackPaint.strokeWidth = strokeWidthPx
        progressPaint.strokeWidth = strokeWidthPx
        trackPaint.color = trackColor
        progressPaint.color = ringColor
    }

    private fun dp(value: Float): Float =
        value * resources.displayMetrics.density
}
