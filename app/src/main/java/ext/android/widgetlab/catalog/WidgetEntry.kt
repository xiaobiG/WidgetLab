package ext.android.widgetlab.catalog

import android.app.Activity

data class WidgetEntry(
    val id: String,
    val title: String,
    val description: String,
    val tag: WidgetTag,
    val demoActivityClass: Class<out Activity>
)

enum class WidgetTag(val labelRes: Int) {
    CANVAS(ext.android.widgetlab.R.string.tag_canvas),
    LAYOUT(ext.android.widgetlab.R.string.tag_layout)
}
