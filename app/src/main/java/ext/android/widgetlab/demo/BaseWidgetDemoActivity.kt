package ext.android.widgetlab.demo

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ext.android.widgetlab.databinding.ActivityWidgetDemoBinding

abstract class BaseWidgetDemoActivity : AppCompatActivity() {

    protected abstract val widgetTitle: String
    protected abstract fun createTargetView(): View
    protected abstract fun buildDemoParams(targetView: View): List<DemoParam>
    protected abstract fun buildXmlSnippet(): String
    protected open fun buildApiSnippet(): String = ""

    private lateinit var binding: ActivityWidgetDemoBinding
    private lateinit var targetView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.demoToolbar.apply {
            title = widgetTitle
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }

        targetView = createTargetView()
        binding.previewHost.addView(
            targetView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )

        ConfigPanelAdapter(binding.configPanel).bindParams(buildDemoParams(targetView))
        binding.xmlSnippet.text = buildXmlSnippet()
        binding.apiSnippet.text = buildApiSnippet()
    }

    protected fun updateXmlSnippet(snippet: String) {
        binding.xmlSnippet.text = snippet
    }

    protected fun updateApiSnippet(snippet: String) {
        binding.apiSnippet.text = snippet
    }
}
