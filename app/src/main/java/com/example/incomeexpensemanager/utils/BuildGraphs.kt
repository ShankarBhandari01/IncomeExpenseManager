package com.example.incomeexpensemanager.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.BarchartLayoutBinding
import com.example.incomeexpensemanager.databinding.GraphsLayoutBinding
import com.example.incomeexpensemanager.model.Transaction
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

class BuildGraphs(private val data: List<Transaction>, private val context: Context) {

    fun getPieChart(graphsLayoutBinding: GraphsLayoutBinding) = with(graphsLayoutBinding) {

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 40f // Set the hole radius (adjust as needed)
        pieChart.transparentCircleRadius = 35f
        pieChart.setDrawCenterText(true)
        pieChart.setRotationAngle(0f)
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // Transform transaction data into PieEntry objects
        val entries: ArrayList<PieEntry> = ArrayList()
        var totalIncome = 0.0
        var totalExpense = 0.0

        for (transaction in data) {
            if (transaction.amount >= 0 && transaction.transactionType.equals("Income", true)) {
                totalIncome += transaction.amount
            } else {
                totalExpense += transaction.amount
            }
        }

        entries.add(PieEntry(totalIncome.toFloat(), "Income"))
        entries.add(PieEntry(totalExpense.toFloat(), "Expense"))

        val dataSet = PieDataSet(entries, "Income vs Expense")
        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(context.resources.getColor(R.color.income))
        colors.add(context.resources.getColor(R.color.expense))

        dataSet.colors = colors

        // Set pie chart data
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(15f)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
        pieData.setValueTextColor(Color.WHITE)
        pieChart.data = pieData

        // Undo all highlights
        pieChart.highlightValues(null)

        // Load chart
        pieChart.invalidate()

    }


    fun buildBarChart(barchartLayoutBinding: BarchartLayoutBinding) = with(barchartLayoutBinding) {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)

        barChart.description.isEnabled = false

        val yAxisLeft: YAxis = barChart.axisLeft
        yAxisLeft.axisMinimum = 2f

        barChart.axisRight.isEnabled = false
        val labels: ArrayList<String> = ArrayList()
        val entries: ArrayList<BarEntry> = ArrayList()
        data.forEachIndexed { index, transaction ->
            val value = transaction.amount.toFloat()
            val transactionTag = transaction.tag
            entries.add(BarEntry(index.toFloat(), value, transactionTag))
            labels.add(transactionTag)
        }
        val dataSet = BarDataSet(entries, "")
        dataSet.colors = ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS)

        val barData = BarData(dataSet)
        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.setCenterAxisLabels(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setGranularity(1F)
        xAxis.isGranularityEnabled = true
        barChart.setDragEnabled(true)
        barChart.setVisibleXRangeMaximum(3F)

        barData.barWidth = 0.15f
        barChart.xAxis.setAxisMinimum(0F)

        barChart.animate()
        barChart.data = barData
        barChart.legend.isEnabled = false
        barChart.invalidate()

    }
}