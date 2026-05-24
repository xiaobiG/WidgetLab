package ext.android.widgetlab.catalog

import ext.android.widgetlab.R
import ext.android.widgetlab.demo.DatePickerDemoActivity
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
        ),
        WidgetEntry(
            id = "date_picker",
            title = "日期选择器",
            description = "圆角弹框日期选择，支持特别日期高亮与主题配色",
            tag = WidgetTag.COMPOSITE,
            demoActivityClass = DatePickerDemoActivity::class.java
        )
    )

    fun titleRes(entry: WidgetEntry): Int = when (entry.id) {
        "ring_progress" -> R.string.ring_progress_title
        "ratio_layout" -> R.string.ratio_layout_title
        "date_picker" -> R.string.date_picker_title
        else -> 0
    }

    fun descriptionRes(entry: WidgetEntry): Int = when (entry.id) {
        "ring_progress" -> R.string.ring_progress_desc
        "ratio_layout" -> R.string.ratio_layout_desc
        "date_picker" -> R.string.date_picker_desc
        else -> 0
    }
}
