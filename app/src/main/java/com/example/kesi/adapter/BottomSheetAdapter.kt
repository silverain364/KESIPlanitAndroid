package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.model.BottomSheetScheduleDto

class BottomSheetAdapter(private val items: ArrayList<BottomSheetScheduleDto>) :
    RecyclerView.Adapter<BottomSheetAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_icon)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val time: TextView = itemView.findViewById(R.id.item_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bottomsheetschedule, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconResId)
        holder.title.text = item.title
        holder.time.text = item.time
    }

    override fun getItemCount(): Int = items.size

    fun reset() {
        val size = items.size
        items.clear()
        this.notifyItemRangeRemoved(0, size) //Todo. Exception 확인
    }

    fun addItem(bottomSheetScheduleDto: BottomSheetScheduleDto) {
        items.add(bottomSheetScheduleDto)
        this.notifyItemInserted(items.lastIndex)
    }
}
