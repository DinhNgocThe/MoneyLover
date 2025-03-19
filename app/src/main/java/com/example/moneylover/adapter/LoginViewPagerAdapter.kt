package com.example.moneylover.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneylover.R

class LoginViewPagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<LoginViewPagerAdapter.LoginImageViewHolder>() {

    inner class LoginImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgItemViewPager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_pager, parent, false)
        return LoginImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoginImageViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
