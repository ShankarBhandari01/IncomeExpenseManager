package com.example.incomeexpensemanager.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivityDashboardBinding
import com.example.incomeexpensemanager.databinding.AddTransactionBinding
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.ui.adapter.CategoryAdaptor
import com.example.incomeexpensemanager.ui.adapter.TransactionAdapter
import com.example.incomeexpensemanager.utils.Constants
import com.example.incomeexpensemanager.utils.SweetToast
import com.example.incomeexpensemanager.utils.UiState
import com.example.incomeexpensemanager.viewmodel.UserLoginVM
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hide
import kotlinx.coroutines.launch
import nepaliRupee
import parseDouble
import show
import transformIntoDatePicker
import java.util.Date

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDashboardBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<UserLoginVM>()
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var categoryAdaptor: CategoryAdaptor

    companion object {
        lateinit var user: User
        fun getIntent(context: Context, user: User): Intent {
            this.user = user
            return Intent(context, DashboardActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRV()
        binding.agent = user
        // picture
        setUpPic()

        binding.btnAddTransaction.setOnClickListener {
            openAddTransactionSheet()
        }
        observeTransaction()
        observeFilter()
        swipeToDelete()
        setUpSpinner()

        binding.image.setOnClickListener {
            startActivity(Profile.getIntent(this, user))
        }
    }

    private fun setUpPic() = with(binding) {
        Glide.with(this@DashboardActivity).load(user.profile).centerCrop()
            .placeholder(R.drawable.profile).into(image)
    }

    private fun openAddTransactionSheet() {
        val dialog = BottomSheetDialog(this@DashboardActivity)
        dialog.setCancelable(true)
        val addtrancation = AddTransactionBinding.inflate(layoutInflater)
        dialog.setContentView(addtrancation.getRoot())
        initDialogViews(addtrancation, dialog)
        return dialog.show()
    }

    private fun initDialogViews(dialogbinding: AddTransactionBinding, dialog: Dialog) =
        with(dialogbinding) {

            val transactionTypeAdapter = ArrayAdapter(
                this@DashboardActivity, R.layout.item_autocomplete_layout, Constants.transactionType
            )
            val tagsAdapter = ArrayAdapter(
                this@DashboardActivity, R.layout.item_autocomplete_layout, Constants.transactionTags
            )

            // Set list to TextInputEditText adapter
            addTransactionLayout.etTransactionType.setAdapter(transactionTypeAdapter)
            addTransactionLayout.etTag.setAdapter(tagsAdapter)

            // Transform TextInputEditText to DatePicker using Ext function
            addTransactionLayout.etWhen.transformIntoDatePicker(
                this@DashboardActivity, "dd/MM/yyyy", Date()
            )
            btnSaveTransaction.setOnClickListener {
                dialogbinding.addTransactionLayout.apply {
                    val (title, amount, transactionType, tag, date, note) = getTransactionContent(
                        dialogbinding
                    )
                    // validate if transaction content is empty or not
                    when {
                        title.isEmpty() -> {
                            this.etTitle.error = "Title must not be empty"
                        }

                        amount.isNaN() -> {
                            this.etAmount.error = "Amount must not be empty"
                        }

                        transactionType.isEmpty() -> {
                            this.etTransactionType.error = "Transaction type must not be empty"
                        }

                        tag.isEmpty() -> {
                            this.etTag.error = "Tag must not be empty"
                        }

                        date.isEmpty() -> {
                            this.etWhen.error = "Date must not be empty"
                        }

                        note.isEmpty() -> {
                            this.etNote.error = "Note must not be empty"
                        }

                        else -> {
                            viewModel.insertTransaction(getTransactionContent(dialogbinding)).run {
                                    SweetToast.success(
                                        this@DashboardActivity, getString(R.string.add_transaction)
                                    )
                                    dialog.dismiss()
                                }
                        }
                    }
                }
            }

        }

    private fun getTransactionContent(dialogbinding: AddTransactionBinding): Transaction =
        dialogbinding.addTransactionLayout.let {
            val title = it.etTitle.text.toString()
            val amount = parseDouble(it.etAmount.text.toString())
            val transactionType = it.etTransactionType.text.toString()
            val tag = it.etTag.text.toString()
            val date = it.etWhen.text.toString()
            val note = it.etNote.text.toString()
            return Transaction(title, amount, transactionType, tag, date, note)
        }


    private fun observeFilter() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.transactionFilter.collect { filter ->
                    when (filter) {
                        "Overall" -> {
                            totalBalanceView.totalBalanceTitle.text =
                                getString(R.string.text_total_balance)
                            totalIncomeExpenseView.show()
                            incomeCardView.totalTitle.text = getString(R.string.text_total_income)
                            expenseCardView.totalTitle.text = getString(R.string.text_total_expense)
                            expenseCardView.totalIcon.setImageResource(R.drawable.ic_expense)
                        }

                        "Income" -> {
                            totalBalanceView.totalBalanceTitle.text =
                                getString(R.string.text_total_income)
                            totalIncomeExpenseView.hide()
                        }

                        "Expense" -> {
                            totalBalanceView.totalBalanceTitle.text =
                                getString(R.string.text_total_expense)
                            totalIncomeExpenseView.hide()
                        }
                    }
                    viewModel.getAllTransaction(filter)
                }
            }

        }
    }

    private fun showAllViews() = with(binding) {
        dashboardGroup.show()
        transactionRv.show()
    }

    private fun hideAllViews() = with(binding) {
        dashboardGroup.hide()
    }

    private fun setupRV() = with(binding) {
        transactionAdapter = TransactionAdapter()
        transactionRv.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@DashboardActivity)
        }
        categoryAdaptor = CategoryAdaptor(Constants.transactionTags, this@DashboardActivity)
        recview.apply {
            adapter = categoryAdaptor
        }

        //
    }

    private fun onTransactionLoaded(list: List<Transaction>) =
        transactionAdapter.differ.submitList(list)


    private fun onTotalTransactionLoaded(transaction: List<Transaction>) = with(binding) {
        val (totalIncome, totalExpense) = transaction.partition { it.transactionType == "Income" }
        val income = totalIncome.sumOf { it.amount }
        val expense = totalExpense.sumOf { it.amount }
        incomeCardView.total.text = "+ ".plus(nepaliRupee(income))
        expenseCardView.total.text = "- ".plus(nepaliRupee(expense))
        totalBalanceView.totalBalance.text = nepaliRupee(income - expense)
    }


    private fun observeTransaction() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {}

                    is UiState.Success -> {
                        showAllViews()
                        onTransactionLoaded(uiState.data)
                        setUpPieChart(uiState.data)
                        onTotalTransactionLoaded(uiState.data)
                    }

                    is UiState.Error -> {
                        SweetToast.error(this@DashboardActivity, uiState.message)
                    }

                    is UiState.Empty -> {
                        hideAllViews()
                    }
                }
            }
        }

    }

    private fun swipeToDelete() {
        // init item touch callback for swipe action
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                transactionAdapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = transactionAdapter.differ.currentList[position]
                val transactionItem = Transaction(
                    transaction.title,
                    transaction.amount,
                    transaction.transactionType,
                    transaction.tag,
                    transaction.date,
                    transaction.note,
                    transaction.createdAt,
                    transaction.id
                )
                viewModel.deleteTransaction(transactionItem)

                Snackbar.make(
                    binding.root,
                    getString(R.string.success_transaction_delete),
                    Snackbar.LENGTH_LONG
                ).apply {
                        setAction(getString(R.string.text_undo)) {
                            viewModel.insertTransaction(
                                transactionItem
                            )
                        }
                        show()
                    }
            }
        }


        val itemDragAndDrop = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,  // Drag directions
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                categoryAdaptor.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Not used for drag and drop
            }
        }


        // attach swipe callback to rv
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.transactionRv)
        }

        ItemTouchHelper(itemDragAndDrop).apply {
            attachToRecyclerView(binding.recview)
        }

    }


    private fun setUpSpinner() = with(binding) {
        val adapter = ArrayAdapter.createFromResource(
            this@DashboardActivity, R.array.allFilters, R.layout.item_filter_dropdown
        )
        adapter.setDropDownViewResource(R.layout.item_filter_dropdown)
        titleRecentTransaction.adapter = adapter

        titleRecentTransaction.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                            when (position) {
                                0 -> {
                                    viewModel.overall()
                                    (view as TextView).setTextColor(resources.getColor(R.color.black))
                                }

                                1 -> {
                                    viewModel.allIncome()
                                    (view as TextView).setTextColor(resources.getColor(R.color.black))
                                }

                                2 -> {
                                    viewModel.allExpense()
                                    (view as TextView).setTextColor(resources.getColor(R.color.black))
                                }
                            }
                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                            viewModel.overall()
                        }

                    }
                }
            }
    }


    private fun setUpPieChart(data: List<Transaction>) = with(binding.graphs) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 40f // Set the hole radius (adjust as needed)
        pieChart.transparentCircleRadius =
            35f // Set the transparent circle radius (adjust as needed)
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
        colors.add(resources.getColor(R.color.income))
        colors.add(resources.getColor(R.color.expense))

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


}