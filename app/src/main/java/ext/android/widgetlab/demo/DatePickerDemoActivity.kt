package ext.android.widgetlab.demo

import android.graphics.Color
import android.view.View
import ext.android.widgets.composite.datepicker.CalendarHelper
import ext.android.widgets.composite.datepicker.DatePickerDialog
import ext.android.widgets.composite.datepicker.DatePickerPreviewView
import ext.android.widgets.composite.datepicker.DatePickerTheme
import ext.android.widgets.composite.datepicker.HighlightStyle
import ext.android.widgets.composite.datepicker.MarkedDate

class DatePickerDemoActivity : BaseWidgetDemoActivity() {

    override val widgetTitle: String = "日期选择器"

    private lateinit var previewView: DatePickerPreviewView

    private var titleIndex = 0
    private var primaryColorIndex = 0
    private var secondaryColorIndex = 0
    private var textColorIndex = 0
    private var highlightColorIndex = 2
    private var markedDatesEnabled = true
    private var highlightStyleIndex = 2

    private val titleOptions = listOf("选择日期", "选择出行日期", "选择生日")

    private val primaryColors = listOf(
        Color.parseColor("#1976D2"),
        Color.parseColor("#D32F2F"),
        Color.parseColor("#388E3C"),
        Color.parseColor("#F57C00")
    )

    private val secondaryColors = listOf(
        Color.parseColor("#455A64"),
        Color.parseColor("#5D4037"),
        Color.parseColor("#37474F"),
        Color.parseColor("#6A1B9A")
    )

    private val textColors = listOf(
        Color.parseColor("#212121"),
        Color.parseColor("#263238"),
        Color.parseColor("#1B1B1B"),
        Color.parseColor("#333333")
    )

    private val highlightColors = listOf(
        Color.parseColor("#1976D2"),
        Color.parseColor("#D32F2F"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#388E3C")
    )

    override fun createTargetView(): View {
        previewView = DatePickerPreviewView(this)
        previewView.updateDialogConfigProvider { buildDialogBuilder() }
        return previewView
    }

    override fun buildDemoParams(targetView: View): List<DemoParam> {
        return listOf(
            DemoParam.ChoiceParam(
                label = "标题",
                options = titleOptions,
                initialIndex = titleIndex
            ) { index ->
                titleIndex = index
                refreshConfig()
            },
            DemoParam.ChoiceParam(
                label = "主色调",
                options = listOf("蓝色", "红色", "绿色", "橙色"),
                initialIndex = primaryColorIndex
            ) { index ->
                primaryColorIndex = index
                refreshConfig()
            },
            DemoParam.ChoiceParam(
                label = "辅助色调",
                options = listOf("蓝灰", "棕色", "深灰", "紫色"),
                initialIndex = secondaryColorIndex
            ) { index ->
                secondaryColorIndex = index
                refreshConfig()
            },
            DemoParam.ChoiceParam(
                label = "文本颜色",
                options = listOf("深灰 1", "深灰 2", "近黑", "深灰 3"),
                initialIndex = textColorIndex
            ) { index ->
                textColorIndex = index
                refreshConfig()
            },
            DemoParam.ChoiceParam(
                label = "高亮颜色",
                options = listOf("蓝色", "红色", "橙色", "绿色"),
                initialIndex = highlightColorIndex
            ) { index ->
                highlightColorIndex = index
                refreshConfig()
            },
            DemoParam.SwitchParam(
                label = "演示特别日期",
                initial = markedDatesEnabled
            ) { enabled ->
                markedDatesEnabled = enabled
                refreshConfig()
            },
            DemoParam.ChoiceParam(
                label = "特别日期样式",
                options = listOf("下标", "背景", "混合"),
                initialIndex = highlightStyleIndex
            ) { index ->
                highlightStyleIndex = index
                refreshConfig()
            }
        )
    }

    override fun buildXmlSnippet(): String = buildXmlSnippetInternal()

    override fun buildApiSnippet(): String = buildApiSnippetInternal()

    private fun refreshConfig() {
        previewView.updateDialogConfigProvider { buildDialogBuilder() }
        refreshSnippets()
    }

    private fun refreshSnippets() {
        updateXmlSnippet(buildXmlSnippetInternal())
        updateApiSnippet(buildApiSnippetInternal())
    }

    private fun buildDialogBuilder(): DatePickerDialog.Builder {
        return DatePickerDialog.Builder(this)
            .setTitle(titleOptions[titleIndex])
            .setTheme(currentTheme())
            .setMarkedDates(buildMarkedDates())
    }

    private fun currentTheme(): DatePickerTheme {
        return DatePickerTheme(
            primaryColor = primaryColors[primaryColorIndex],
            secondaryColor = secondaryColors[secondaryColorIndex],
            textColor = textColors[textColorIndex],
            highlightColor = highlightColors[highlightColorIndex]
        )
    }

    private fun buildMarkedDates(): Set<MarkedDate> {
        if (!markedDatesEnabled) return emptySet()
        val today = CalendarHelper.today()
        val (nextYear, nextMonth) = CalendarHelper.addMonths(today.year, today.month, 1)
        val style = when (highlightStyleIndex) {
            0 -> HighlightStyle.DOT
            1 -> HighlightStyle.BACKGROUND
            else -> null
        }
        if (style != null) {
            return setOf(
                MarkedDate(today.year, today.month, 8, style),
                MarkedDate(today.year, today.month, 15, style),
                MarkedDate(nextYear, nextMonth, 1, style)
            )
        }
        return setOf(
            MarkedDate(today.year, today.month, 8, HighlightStyle.DOT),
            MarkedDate(today.year, today.month, 15, HighlightStyle.BACKGROUND),
            MarkedDate(nextYear, nextMonth, 1, HighlightStyle.DOT)
        )
    }

    private fun buildXmlSnippetInternal(): String {
        return """
            <!-- Dialog 类控件无 XML 声明，以下为 DatePickerPanelView 可选 attrs 参考 -->
            <ext.android.widgets.composite.datepicker.DatePickerPanelView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:datePickerPrimaryColor="${colorHex(primaryColors[primaryColorIndex])}"
                app:datePickerSecondaryColor="${colorHex(secondaryColors[secondaryColorIndex])}"
                app:datePickerTextColor="${colorHex(textColors[textColorIndex])}"
                app:datePickerHighlightColor="${colorHex(highlightColors[highlightColorIndex])}" />
        """.trimIndent()
    }

    private fun buildApiSnippetInternal(): String {
        val theme = currentTheme()
        return """
            DatePickerDialog.Builder(context)
                .setTitle("${titleOptions[titleIndex]}")
                .setTheme(
                    DatePickerTheme(
                        primaryColor = Color.parseColor("${colorHex(theme.primaryColor)}"),
                        secondaryColor = Color.parseColor("${colorHex(theme.secondaryColor)}"),
                        textColor = Color.parseColor("${colorHex(theme.textColor)}"),
                        highlightColor = Color.parseColor("${colorHex(theme.highlightColor)}")
                    )
                )
                .setMarkedDates(
                    setOf(
                        MarkedDate(2026, 5, 8, HighlightStyle.DOT),
                        MarkedDate(2026, 5, 15, HighlightStyle.BACKGROUND)
                    )
                )
                .setOnDateSelectedListener { year, month, day ->
                    // 仅点击「确定」时回调
                }
                .setOnCancelListener {
                    // 取消 / 返回 / 点击外部
                }
                .show()
        """.trimIndent()
    }

    private fun colorHex(color: Int): String =
        String.format("#%06X", 0xFFFFFF and color)
}
