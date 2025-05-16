package com.example.moneylover.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moneylover.R
import com.example.moneylover.data.room.model.ExpenseCategory

class CategoryAdapter(
    private val context: Context,
    private val onClick: (ExpenseCategory) -> Unit,
    private val onLongClick: (ExpenseCategory) -> Unit // To update name of category
) : ListAdapter<ExpenseCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconUrl = itemView.findViewById<ImageView>(R.id.imgIconUrlCategoryRcvItem)
        private val group = itemView.findViewById<TextView>(R.id.txtTypeCategoryRcvItem)
        private val layoutItem = itemView.findViewById<ConstraintLayout>(R.id.categoryRcvItem)

        fun onBind(category: ExpenseCategory) {
            group.text = category.name
            Glide.with(context)
                .load(category.iconUrl)
                .placeholder(R.drawable.ic_type)
                .error(R.drawable.ic_warning)
                .into(iconUrl)

            layoutItem.setOnClickListener {
                onClick(category)
            }

            layoutItem.setOnLongClickListener {
                onLongClick(category)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_recycle_view_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ExpenseCategory>() {
        override fun areItemsTheSame(oldItem: ExpenseCategory, newItem: ExpenseCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseCategory, newItem: ExpenseCategory): Boolean {
            return oldItem == newItem
        }
    }
}
