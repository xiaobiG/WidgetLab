package ext.android.widgetlab.demo

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import ext.android.widgets.canvas.RingProgressView

class RingProgressDemoActivity : BaseWidgetDemoActivity() {

    override val widgetTitle: String = "环形进度"

    private lateinit var ringView: RingProgressView
    private var progressPercent = 35
    private var strokeWidthDp = 10
    private var colorIndex = 0

    private val colorOptions = listOf(
        Color.parseColor("#1976D2"),
        Color.parseColor("#D32F2F"),
        Color.parseColor("#388E3C"),
        Color.parseColor("#F57C00")
    )

    override fun createTargetView(): View {
        ringView = RingProgressView(this).apply {
            setProgress(progressPercent / 100f)
            setRingStrokeWidth(dp(strokeWidthDp))
            setRingColor(colorOptions[colorIndex])
        }
        return ringView
    }

    override fun buildDemoParams(targetView: View): List<DemoParam> {
        return listOf(
            DemoParam.SeekBarParam(
                label = "进度",
                min = 0,
                max = 100,
                initial = progressPercent,
                valueFormatter = { "$it%" }
            ) { value ->
                progressPercent = value
                ringView.setProgress(value / 100f)
                refreshSnippets()
            },
            DemoParam.SeekBarParam(
                label = "线宽 (dp)",
                min = 4,
                max = 24,
                initial = strokeWidthDp,
                valueFormatter = { "${it}dp" }
            ) { value ->
                strokeWidthDp = value
                ringView.setRingStrokeWidth(dp(value))
                refreshSnippets()
            },
            DemoParam.ChoiceParam(
                label = "进度颜色",
                options = listOf("蓝色", "红色", "绿色", "橙色"),
                initialIndex = colorIndex
            ) { index ->
                colorIndex = index
                ringView.setRingColor(colorOptions[index])
                refreshSnippets()
            }
        )
    }

    override fun buildXmlSnippet(): String = buildXmlSnippetInternal()

    override fun buildApiSnippet(): String = buildApiSnippetInternal()

    override fun onResume() {
        super.onResume()
        refreshSnippets()
    }

    private fun refreshSnippets() {
        updateXmlSnippet(buildXmlSnippetInternal())
        updateApiSnippet(buildApiSnippetInternal())
    }

    private fun buildXmlSnippetInternal(): String {
        val colorHex = String.format("#%06X", 0xFFFFFF and colorOptions[colorIndex])
        return """
            <ext.android.widgets.canvas.RingProgressView
                android:layout_width="120dp"
                android:layout_height="120dp"
                app:ringProgress="${progressPercent / 100f}"
                app:ringStrokeWidth="${strokeWidthDp}dp"
                app:ringColor="$colorHex"
                app:ringTrackColor="#E0E0E0" />
        """.trimIndent()
    }

    private fun buildApiSnippetInternal(): String {
        val colorHex = String.format("#%06X", 0xFFFFFF and colorOptions[colorIndex])
        return """
            ringProgressView.setProgress(${progressPercent / 100f}f)
            ringProgressView.setRingStrokeWidth(${strokeWidthDp}f * density)
            ringProgressView.setRingColor(Color.parseColor("$colorHex"))
        """.trimIndent()
    }

    private fun dp(value: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        )
    }
}
