package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.fragment.ScheduleBottomSheet
import com.example.kesi.model.BottomSheetScheduleDto

class BottomSheetAdapter(private val items: ArrayList<BottomSheetScheduleDto>) :
    RecyclerView.Adapter<BottomSheetAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.item_icon)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val time: TextView = itemView.findViewById(R.id.item_time)

        fun bind(bottomSheetScheduleDto: BottomSheetScheduleDto) {
            icon.setImageResource(bottomSheetScheduleDto.iconResId)
            title.text = bottomSheetScheduleDto.title
            time.text = bottomSheetScheduleDto.time

            itemView.setOnClickListener {
                
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bottomsheetschedule, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
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
