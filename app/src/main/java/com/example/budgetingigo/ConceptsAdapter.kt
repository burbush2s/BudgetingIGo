package com.example.budgetingigo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetingigo.databinding.ConceptsItemBinding

class ConceptsAdapter( private val items: List<Pair<String,Float?>>,
                       private val onColorChange: (Boolean) -> Unit
                       )
: RecyclerView.Adapter<ConceptsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ConceptsItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ConceptsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conceptPercentage =items[position]

        with(holder.binding) {
            val hasNegativeValue: Boolean
            if(conceptPercentage.second != null){
                descriptionText.text = conceptPercentage.first + conceptPercentage.second
                hasNegativeValue = if(conceptPercentage.second!! <0) {
                    descriptionText.setTextColor(Color.RED)
                    true
                } else {
                    descriptionText.setTextColor(Color.BLACK)
                    false
                }
                onColorChange(hasNegativeValue)
            }else
                descriptionText.text = conceptPercentage.first

        }

    }


    override fun getItemCount(): Int = items.size
}