package com.example.kesi.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}