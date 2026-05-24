package ext.android.widgetlab.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ext.android.widgetlab.R
import ext.android.widgetlab.catalog.WidgetCatalog
import ext.android.widgetlab.catalog.WidgetEntry
import ext.android.widgetlab.catalog.WidgetTag
import ext.android.widgetlab.databinding.ItemWidgetEntryBinding

class WidgetListAdapter(
    private val onItemClick: (WidgetEntry) -> Unit
) : ListAdapter<WidgetEntry, WidgetListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWidgetEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(
        private val binding: ItemWidgetEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: WidgetEntry, onItemClick: (WidgetEntry) -> Unit) {
            val context = binding.root.context
            val titleRes = WidgetCatalog.titleRes(entry)
            val descRes = WidgetCatalog.descriptionRes(entry)

            binding.entryTitle.text = if (titleRes != 0) context.getString(titleRes) else entry.title
            binding.entryDescription.text =
                if (descRes != 0) context.getString(descRes) else entry.description

            binding.entryTag.text = context.getString(entry.tag.labelRes)
            val chipColor = when (entry.tag) {
                WidgetTag.CANVAS -> R.color.chip_canvas
                WidgetTag.LAYOUT -> R.color.chip_layout
                WidgetTag.COMPOSITE -> R.color.chip_composite
            }
            binding.entryTag.chipBackgroundColor =
                ContextCompat.getColorStateList(context, chipColor)

            binding.root.setOnClickListener { onItemClick(entry) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<WidgetEntry>() {
        override fun areItemsTheSame(oldItem: WidgetEntry, newItem: WidgetEntry): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WidgetEntry, newItem: WidgetEntry): Boolean =
            oldItem == newItem
    }
}
