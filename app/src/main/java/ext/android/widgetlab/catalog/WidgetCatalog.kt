package ext.android.widgetlab.catalog

import ext.android.widgetlab.R
import ext.android.widgetlab.demo.RatioLayoutDemoActivity
import ext.android.widgetlab.demo.RingProgressDemoActivity

object WidgetCatalog {
    val entries: List<WidgetEntry> = listOf(
        WidgetEntry(
            id = "ring_progress",
            title = "环形进度",
            description = "Canvas 绘制的圆环进度控件",
            tag = WidgetTag.CANVAS,
            demoActivityClass = RingProgressDemoActivity::class.java
        ),
        WidgetEntry(
            id = "ratio_layout",
            title = "比例容器",
            description = "按宽度和比例自动计算高度的 FrameLayout",
            tag = WidgetTag.LAYOUT,
            demoActivityClass = RatioLayoutDemoActivity::class.java
        )
    )

    fun titleRes(entry: WidgetEntry): Int = when (entry.id) {
        "ring_progress" -> R.string.ring_progress_title
        "ratio_layout" -> R.string.ratio_layout_title
        else -> 0
    }

    fun descriptionRes(entry: WidgetEntry): Int = when (entry.id) {
        "ring_progress" -> R.string.ring_progress_desc
        "ratio_layout" -> R.string.ratio_layout_desc
        else -> 0
    }
}
