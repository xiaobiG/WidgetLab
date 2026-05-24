package ext.android.widgetlab.demo

sealed class DemoParam {
    abstract val label: String

    data class SeekBarParam(
        override val label: String,
        val min: Int,
        val max: Int,
        val initial: Int,
        val valueFormatter: (Int) -> String = { it.toString() },
        val onChange: (Int) -> Unit
    ) : DemoParam()

    data class SwitchParam(
        override val label: String,
        val initial: Boolean,
        val onChange: (Boolean) -> Unit
    ) : DemoParam()

    data class ChoiceParam(
        override val label: String,
        val options: List<String>,
        val initialIndex: Int = 0,
        val onChange: (Int) -> Unit
    ) : DemoParam()
}
