package com.example.kesi.ui.community

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}