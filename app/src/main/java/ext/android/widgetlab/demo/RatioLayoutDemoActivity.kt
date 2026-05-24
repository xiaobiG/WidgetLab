package ext.android.widgetlab.demo

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ext.android.widgets.layout.RatioLayoutView

class RatioLayoutDemoActivity : BaseWidgetDemoActivity() {

    override val widgetTitle: String = "比例容器"

    private lateinit var ratioLayout: RatioLayoutView
    private var ratioPercent = 75

    override fun createTargetView(): View {
        ratioLayout = RatioLayoutView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setRatio(ratioPercent / 100f)
            setBackgroundColor(Color.parseColor("#FFE3F2FD"))

            addView(
                TextView(context).apply {
                    text = "子 View 居中\nratio = ${ratioPercent}%"
                    gravity = Gravity.CENTER
                    setTextColor(Color.parseColor("#1565C0"))
                    setBackgroundColor(Color.parseColor("#FF90CAF9"))
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            )
        }
        return ratioLayout
    }

    override fun buildDemoParams(targetView: View): List<DemoParam> {
        return listOf(
            DemoParam.SeekBarParam(
                label = "高宽比 (%)",
                min = 30,
                max = 150,
                initial = ratioPercent,
                valueFormatter = { "$it%" }
            ) { value ->
                ratioPercent = value
                ratioLayout.setRatio(value / 100f)
                updateChildLabel()
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

    private fun updateChildLabel() {
        val child = ratioLayout.getChildAt(0) as? TextView ?: return
        child.text = "子 View 居中\nratio = ${ratioPercent}%"
    }

    private fun refreshSnippets() {
        updateXmlSnippet(buildXmlSnippetInternal())
        updateApiSnippet(buildApiSnippetInternal())
    }

    private fun buildXmlSnippetInternal(): String {
        return """
            <ext.android.widgets.layout.RatioLayoutView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:layoutRatio="${ratioPercent / 100f}">

                <!-- 子 View 按容器尺寸布局 -->

            </ext.android.widgets.layout.RatioLayoutView>
        """.trimIndent()
    }

    private fun buildApiSnippetInternal(): String {
        return """
            ratioLayoutView.setRatio(${ratioPercent / 100f}f)
            ratioLayoutView.getRatio()
        """.trimIndent()
    }
}
