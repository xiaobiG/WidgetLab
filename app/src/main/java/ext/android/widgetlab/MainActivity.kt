package ext.android.widgetlab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ext.android.widgetlab.catalog.WidgetCatalog
import ext.android.widgetlab.databinding.ActivityMainBinding
import ext.android.widgetlab.ui.WidgetListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adapter = WidgetListAdapter { entry ->
            startActivity(Intent(this, entry.demoActivityClass))
        }
        binding.widgetList.layoutManager = LinearLayoutManager(this)
        binding.widgetList.adapter = adapter
        adapter.submitList(WidgetCatalog.entries)
    }
}
