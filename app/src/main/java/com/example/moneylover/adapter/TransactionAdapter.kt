package com.example.moneylover.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.TransactionWithCategory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val context: Context,
    private val onClick: (TransactionWithCategory) -> Unit
) : ListAdapter<TransactionWithCategory, TransactionAdapter.TransactionViewHolder>(DiffCallback()) {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon =  itemView.findViewById<ImageView>(R.id.imgCategoryItemRcvTransaction)
        private val categoryName = itemView.findViewById<TextView>(R.id.txtCategoryNameItemRcvTransaction)
        private val date = itemView.findViewById<TextView>(R.id.txtDateItemRcvTransaction)
        private val amount = itemView.findViewById<TextView>(R.id.txtAmountItemRcvTransaction)
        private val layoutItem = itemView.findViewById<CardView>(R.id.itemRcvTransaction)

        @SuppressLint("SetTextI18n")
        fun onBind(transaction: TransactionWithCategory) {
            categoryName.text = transaction.name
            date.text = simpleDateFormat.format(transaction.date)
            amount.text = formatCurrency(transaction.amount) + " " + context.getString(R.string.vnd)
            Glide.with(context)
                .load(transaction.iconUrl)
                .placeholder(R.drawable.ic_type)
                .error(R.drawable.ic_warning)
                .into(icon)

            layoutItem.setOnClickListener {
                onClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rcv_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionWithCategory>() {
        override fun areItemsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        private val currencyFormatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale("vi", "VN")))

        private fun formatCurrency(value: Double): String {
            return currencyFormatter.format(value)
        }
    }
}