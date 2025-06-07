package com.example.moneylover.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.BudgetWithCategory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class BudgetAdapter(
    private val context: Context,
    private val onDelete: (BudgetWithCategory) -> Unit
) : ListAdapter<BudgetWithCategory, BudgetAdapter.BudgetViewHolder>(DiffCallback()) {
    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconUrl = itemView.findViewById<ImageView>(R.id.imgCategoryItemRcvBudget)
        private val categoryName = itemView.findViewById<TextView>(R.id.txtCategoryNameItemRcvBudget)
        private val remainingAmount = itemView.findViewById<TextView>(R.id.txtRemainingAmountItemRcvBudget)
        private val totalBudget = itemView.findViewById<TextView>(R.id.txtTotalItemRcvBudget)
        private val totalSpent = itemView.findViewById<TextView>(R.id.txtSpentItemRcvBudget)
        private val delete = itemView.findViewById<ImageButton>(R.id.btnDeleteBudgetBudgetFragment)

        @SuppressLint("SetTextI18n")
        fun onBind(budget: BudgetWithCategory) {
            Glide.with(context)
                .load(budget.iconUrl)
                .placeholder(R.drawable.ic_type)
                .error(R.drawable.ic_warning)
                .into(iconUrl)

            categoryName.text = budget.name
            totalBudget.text = formatCurrency(budget.amount)
            totalSpent.text = formatCurrency(budget.spent)
            val remaining = budget.amount - budget.spent
            if (remaining < 0) {
                remainingAmount.setTextColor(context.getColor(R.color.red))
            } else {
                remainingAmount.setTextColor(context.getColor(R.color.main_color))
            }
            remainingAmount.text = formatCurrency(remaining) + " " + context.getString(R.string.vnd)

            delete.setOnClickListener {
                onDelete(budget)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rcv_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BudgetViewHolder,
        position: Int
    ) {
        return holder.onBind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<BudgetWithCategory>() {
        override fun areItemsTheSame(oldItem: BudgetWithCategory, newItem: BudgetWithCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BudgetWithCategory, newItem: BudgetWithCategory): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private val currencyFormatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale("vi", "VN")))

        private fun formatCurrency(value: Double): String {
            return currencyFormatter.format(value)
        }
    }
}