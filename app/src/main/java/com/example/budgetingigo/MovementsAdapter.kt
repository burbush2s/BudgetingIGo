package com.example.budgetingigo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetingigo.data.Movements
import com.example.budgetingigo.databinding.MovementsItemBinding

class MovementsAdapter ( private val items: List<Movements>)
    : RecyclerView.Adapter<MovementsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MovementsItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementsAdapter.ViewHolder {
        return ViewHolder(
            MovementsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovementsAdapter.ViewHolder, position: Int) {
        val movements =items[position]

        with(holder.binding) {
            description.text = movements.description
            concept.text = movements.concept
            amount.text = movements.amount.toString()
            sign.text = movements.type
            if(movements.type == "+")
                sign.setTextColor(Color.GREEN)
            else
                sign.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int = items.size


}