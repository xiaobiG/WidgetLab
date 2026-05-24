package ext.android.widgetlab.demo

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ext.android.widgetlab.R
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

        binding.darkBackgroundSwitch.setOnCheckedChangeListener { _, isDark ->
            val colorRes = if (isDark) R.color.preview_bg_dark else R.color.preview_bg_light
            binding.previewContainer.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        }

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
