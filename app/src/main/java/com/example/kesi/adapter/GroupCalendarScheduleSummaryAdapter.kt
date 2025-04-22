package com.example.kesi.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R
import com.example.kesi.data.GroupCalendarScheduleSummaryItem
import com.example.kesi.domain.ScheduleType
import com.example.kesi.holder.BindViewHolder
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class GroupCalendarScheduleSummaryAdapter(
    private val items: ArrayList<GroupCalendarScheduleSummaryItem>
) : RecyclerView.Adapter<BindViewHolder<GroupCalendarScheduleSummaryItem>>() {
    companion object {
        private const val VIEW_TYPE_DAY = 1
        private const val VIEW_TYPE_TYPE = 2
        private const val VIEW_TYPE_ITEM = 3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindViewHolder<GroupCalendarScheduleSummaryItem> {
        val makeView = { id: Int -> LayoutInflater.from(parent.context).inflate(id, parent, false) }

        return when (viewType) {
            VIEW_TYPE_DAY -> DayViewHolder(makeView(R.layout.item_group_calendar_schedule_day))
            VIEW_TYPE_TYPE -> TypeViewHolder(makeView(R.layout.item_group_calendar_schedule_type))
            VIEW_TYPE_ITEM -> ItemViewHolder(makeView(R.layout.item_group_calendar_schedule))
            else -> throw RuntimeException("not expect view type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BindViewHolder<GroupCalendarScheduleSummaryItem>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is GroupCalendarScheduleSummaryItem.Day -> VIEW_TYPE_DAY
            is GroupCalendarScheduleSummaryItem.Kind -> VIEW_TYPE_TYPE
            is GroupCalendarScheduleSummaryItem.Item -> VIEW_TYPE_ITEM
        }
    }

    class DayViewHolder(itemView: View) : BindViewHolder<GroupCalendarScheduleSummaryItem>(itemView) {
        private val dayTv = itemView.findViewById<TextView>(R.id.dayTv)
        private val dateFormatter = DateTimeFormatter.ofPattern("dd일 E", Locale.UK)

        override fun bind(item: GroupCalendarScheduleSummaryItem)  {
            val day = item as GroupCalendarScheduleSummaryItem.Day
            dayTv.text = dateFormatter.format(day.date)
        }
    }

    class TypeViewHolder(itemView: View) : BindViewHolder<GroupCalendarScheduleSummaryItem>(itemView) {
        private val listIv = itemView.findViewById<ImageView>(R.id.listIv)
        private val titleTv = itemView.findViewById<TextView>(R.id.titleTv)

        override fun bind(item: GroupCalendarScheduleSummaryItem) {
            val kind = item as GroupCalendarScheduleSummaryItem.Kind

            when (kind.type) {
                ScheduleType.GROUP -> {
                    titleTv.text = "그룹 일정"
                    listIv.setImageResource(R.drawable.ic_group_list_cloud)
                }

                ScheduleType.OTHER -> {
                    titleTv.text = "팀원 일정"
                    listIv.setImageResource(R.drawable.ic_group_list_other)
                }

                ScheduleType.PERSONAL -> {
                    titleTv.text = "개인 일정"
                    listIv.setImageResource(R.drawable.ic_group_list_personal)
                }
            }
        }

    }

    class ItemViewHolder(itemView: View) : BindViewHolder<GroupCalendarScheduleSummaryItem>(itemView) {
        private val markView = itemView.findViewById<View>(R.id.markView)
        private val titleTv = itemView.findViewById<TextView>(R.id.titleTv)
        private val periodTv = itemView.findViewById<TextView>(R.id.periodTv)

        override fun bind(item: GroupCalendarScheduleSummaryItem) {
            val itm = item as GroupCalendarScheduleSummaryItem.Item
            val schedule = itm.schedule

            markView.backgroundTintList = ColorStateList.valueOf(schedule.color.toArgb())
            titleTv.text = schedule.title
        }
    }
}