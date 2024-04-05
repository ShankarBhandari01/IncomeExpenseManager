package com.example.incomeexpensemanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.CategoryLayoutBinding
import com.example.incomeexpensemanager.ui.DashboardActivity
import java.util.Collections


class CategoryAdaptor(var data: List<String>, var context: DashboardActivity) :
    RecyclerView.Adapter<CategoryAdaptor.ViewHolder>(), ItemMoveCallbackListener.Listener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdaptor.ViewHolder {
        return ViewHolder(
            CategoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryAdaptor.ViewHolder, position: Int) {
        holder.binding.title.text = data[position]
        when (data[position]) {
            "Housing" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_food)
            }

            "Transportation" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_transport)
            }

            "Food" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_food)
            }

            "Utilities" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_utilities)
            }

            "Insurance" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_insurance)
            }

            "Healthcare" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_medical)
            }

            "Saving & Debts" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_savings)
            }

            "Personal Spending" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_personal_spending)
            }

            "Entertainment" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_entertainment)
            }

            "Miscellaneous" -> {
                holder.binding.icon.setImageResource(R.drawable.ic_others)
            }

            else -> {
                holder.binding.icon.setImageResource(R.drawable.ic_others)
            }
        }
        holder.binding.imgbtnAdd.setOnClickListener {
            // context.showBottomSheet()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: CategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        TODO("Not yet implemented")
    }


}
interface ItemMoveCallbackListener {
    interface Listener {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }
}