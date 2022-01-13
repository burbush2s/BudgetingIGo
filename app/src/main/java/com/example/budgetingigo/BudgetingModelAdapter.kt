package com.example.budgetingigo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.databinding.BudgetingModelItemBinding

class BudgetingModelAdapter(
    private val items: List<BudgetingModel>,
    private val onItemClick: (BudgetingModel) -> Unit
)
    : RecyclerView.Adapter<BudgetingModelAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BudgetingModelItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BudgetingModelItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]

        with(holder.binding) {
            productImage.load(model.imageFile) {
                crossfade(1000)
            }
            budgetingModelNameText.text = model.name
            descriptionText.text = model.description
        }

        holder.itemView.setOnClickListener {
            onItemClick(model)
        }
    }

    override fun getItemCount(): Int = items.size

}