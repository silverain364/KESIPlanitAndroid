package com.example.kesi.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.activity.AddScheduleActivity
import com.example.kesi.activity.EditScheduleActivity
import com.example.kesi.data.EditScheduleDto
import com.example.kesi.domain.Schedule
import com.example.kesi.fragment.ScheduleBottomSheet
import com.example.kesi.model.BottomSheetScheduleDto

class BottomSheetAdapter(
    private val items: ArrayList<Schedule>,
    private val editScheduleLauncher: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<BottomSheetAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.item_icon)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val time: TextView = itemView.findViewById(R.id.item_time)

        fun bind(schedule: Schedule) {
            icon.backgroundTintList = ColorStateList.valueOf(schedule.color.toArgb())
            title.text = schedule.title
            time.text = schedule.endTime.toString()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditScheduleActivity::class.java)
                intent.putExtra("schedule", EditScheduleDto.from(schedule))
                editScheduleLauncher.launch(intent)
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

    fun addItem(schedule: Schedule) {
        items.add(schedule)
        this.notifyItemInserted(items.lastIndex)
    }
}
