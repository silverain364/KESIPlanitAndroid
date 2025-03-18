package com.example.kesi.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kesi.R

class CircleColorAdapter(
    private val colorList: List<Color>,
    private var initColor: Color = colorList.first()
): RecyclerView.Adapter<CircleColorAdapter.ViewHolder>() {
    init {
        if(!colorList.stream().anyMatch {
                Log.d("CircleColorAdapter", "color : ${it.toArgb()} / initColor : ${initColor.toArgb()}")
                it.toArgb() == initColor.toArgb()
        })
            initColor = colorList.first()

    }

    private lateinit var selectedHolder: ViewHolder
    private var init = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_circle_color, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        p0.bind(colorList[position])

        if(!init && colorList[position] == initColor) { //처음 초기화 시
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