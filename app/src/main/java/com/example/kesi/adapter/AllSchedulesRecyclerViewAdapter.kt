package com.example.kesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.model.AllSchedulesDto

class AllSchedulesRecyclerViewAdapter (
    private val items: ArrayList<AllSchedulesDto>) :
    RecyclerView.Adapter<AllSchedulesRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.item_icon)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val date: TextView = itemView.findViewById(R.id.item_date)
        private val time: TextView = itemView.findViewById(R.id.item_time)

        fun bind(allSchedulesDto: AllSchedulesDto) {
            icon.setImageResource(allSchedulesDto.iconResId)
            title.text = allSchedulesDto.title
            date.text = allSchedulesDto.date + "일"
            time.text = allSchedulesDto.time

            if (position > 0 && allSchedulesDto.date == items[position-1].date) {
                date.visibility = View.GONE
            }

            itemView.setOnClickListener {
                //editScheduleLauncher.launch(Intent(itemView.context, AddScheduleActivity::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_schedules_date, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /*fun reset() {
        val size = items.size
        items.clear()
        this.notifyItemRangeRemoved(0, size) //Todo. Exception 확인
    }

    fun addItem(allSchedulesDto:AllSchedulesDto) {
        items.add(allSchedulesDto)
        this.notifyItemInserted(items.lastIndex)
    }*/
}