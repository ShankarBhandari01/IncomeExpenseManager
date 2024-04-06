package com.example.incomeexpensemanager.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.incomeexpensemanager.databinding.BarchartLayoutBinding
import com.example.incomeexpensemanager.databinding.GraphsLayoutBinding
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.utils.BuildGraphs
import com.example.incomeexpensemanager.utils.SweetToast

class CustomPagerAdapter(private val context: Context, private val dataset: List<Transaction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: ViewBinding = when (viewType) {
            0 -> GraphsLayoutBinding.inflate(inflater, parent, false)
            1 -> BarchartLayoutBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            val buildGraphs = BuildGraphs(dataset, context)
            when (position) {
                0 -> {
                    // Bind data or perform actions specific to the first view
                    val customViewHolder = holder as CustomViewHolder<GraphsLayoutBinding>
                    customViewHolder.binding.apply {
                        buildGraphs.getPieChart(this)
                    }
                }

                1 -> {
                    // Bind data or perform actions specific to the second view
                    val customViewHolder = holder as CustomViewHolder<BarchartLayoutBinding>
                    customViewHolder.binding.apply {
                        buildGraphs.buildBarChart(this)
                    }
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { SweetToast.error(context, it) }
        }

    }


    override fun getItemCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class CustomViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

}

