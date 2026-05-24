package ext.android.widgetlab.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.materialswitch.MaterialSwitch
import ext.android.widgetlab.R

class ConfigPanelAdapter(
    private val container: LinearLayout
) {
    fun bindParams(params: List<DemoParam>) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(container.context)
        params.forEach { param ->
            when (param) {
                is DemoParam.SeekBarParam -> bindSeekBar(inflater, param)
                is DemoParam.SwitchParam -> bindSwitch(inflater, param)
                is DemoParam.ChoiceParam -> bindChoice(inflater, param)
            }
        }
    }

    private fun bindSeekBar(inflater: LayoutInflater, param: DemoParam.SeekBarParam) {
        val itemView = inflater.inflate(R.layout.item_demo_param_seekbar, container, false)
        val label = itemView.findViewById<TextView>(R.id.paramLabel)
        val seekBar = itemView.findViewById<SeekBar>(R.id.paramSeekBar)
        val valueText = itemView.findViewById<TextView>(R.id.paramValue)

        label.text = param.label
        seekBar.max = param.max - param.min
        seekBar.progress = param.initial - param.min
        valueText.text = param.valueFormatter(param.initial)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress + param.min
                valueText.text = param.valueFormatter(value)
                if (fromUser) {
                    param.onChange(value)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        container.addView(itemView)
    }

    private fun bindSwitch(inflater: LayoutInflater, param: DemoParam.SwitchParam) {
        val switchView = inflater.inflate(R.layout.item_demo_param_switch, container, false)
            as MaterialSwitch
        switchView.text = param.label
        switchView.isChecked = param.initial
        switchView.setOnCheckedChangeListener { _, isChecked ->
            param.onChange(isChecked)
        }
        container.addView(switchView)
    }

    private fun bindChoice(inflater: LayoutInflater, param: DemoParam.ChoiceParam) {
        val itemView = inflater.inflate(R.layout.item_demo_param_choice, container, false)
        val label = itemView.findViewById<TextView>(R.id.paramLabel)
        val chipGroup = itemView.findViewById<ChipGroup>(R.id.paramChoiceGroup)

        label.text = param.label
        param.options.forEachIndexed { index, option ->
            val chip = Chip(container.context).apply {
                text = option
                isCheckable = true
                isChecked = index == param.initialIndex
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val checkedChip = group.findViewById<Chip>(checkedIds.first())
            val index = param.options.indexOf(checkedChip.text.toString())
            if (index >= 0) {
                param.onChange(index)
            }
        }

        container.addView(itemView)
    }
}
