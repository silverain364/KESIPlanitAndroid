package com.example.kesi.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R

class CircleColorAdapter(private val colorList: List<Color>): RecyclerView.Adapter<CircleColorAdapter.ViewHolder>() {
    private lateinit var selectedHolder: ViewHolder
    private var init = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_circle_color, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(colorList[p1])

        if(!init && p1 == 0) { //처음 초기화 시
            selectedHolder = p0
            selectedHolder.selectView.visibility = View.VISIBLE
            init = true
        }
    }

    fun getSelectedColor(): Color {
        return selectedHolder.color
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val backgroundView: View = itemView.findViewById(R.id.backgroundView)
        val selectView: View = itemView.findViewById(R.id.selecteView)
        lateinit var color: Color
        fun bind(color: Color) {
            this.color = color
            backgroundView.backgroundTintList = ColorStateList.valueOf(color.toArgb())

            itemView.setOnClickListener {
                selectedHolder.selectView.visibility = View.GONE
                selectedHolder = this
                selectedHolder.selectView.visibility = View.VISIBLE
            }
        }
    }
}