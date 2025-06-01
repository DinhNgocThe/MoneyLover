package com.example.moneylover.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.ExpenseCategoryWithTotal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class TopExpenseAdapter(
    private val context: Context
) : ListAdapter<ExpenseCategoryWithTotal, TopExpenseAdapter.TopExpenseViewHolder>(DiffCallback()) {

    inner class TopExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgIcon = itemView.findViewById<ImageView>(R.id.imgIconItemTopExpenseCategories)
        private val txtName = itemView.findViewById<TextView>(R.id.txtCategoryNameItemTopExpenseCategories)
        private val txtAmount = itemView.findViewById<TextView>(R.id.txtAmountItemTopExpenseCategories)

        @SuppressLint("SetTextI18n")
        fun onBind(category: ExpenseCategoryWithTotal) {
            Glide.with(context)
                .load(category.iconUrl)
                .placeholder(R.drawable.ic_other)
                .error(R.drawable.ic_warning)
                .into(imgIcon)
            txtName.text = category.name
            txtAmount.text = formatCurrency(category.totalAmount) + " " + context.getString(R.string.vnd)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rcv_top_expense_categories, parent, false)
        return TopExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopExpenseViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }

    class DiffCallback : DiffUtil.ItemCallback<ExpenseCategoryWithTotal>() {
        override fun areItemsTheSame(oldItem: ExpenseCategoryWithTotal, newItem: ExpenseCategoryWithTotal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseCategoryWithTotal, newItem: ExpenseCategoryWithTotal): Boolean {
            return oldItem == newItem
        }
    }

}