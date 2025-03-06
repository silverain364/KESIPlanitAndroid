package com.example.kesi.util.view

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.constraintlayout.widget.Guideline

class GuideRender {
    fun createGuideLine(
        constraintLayout: ConstraintLayout,
        vertical: Int,
        horizontal: Int
    ): Pair<ArrayList<Guideline>, ArrayList<Guideline>> {
        val verticalGuide = ArrayList<Guideline>()
        val horizontalGuide = ArrayList<Guideline>()

        //라인 생성
        for (i in 0..vertical) {
            verticalGuide.add(Guideline(constraintLayout.context).apply {
                id = Guideline.generateViewId()
                layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT
                )
                    .apply {
                        orientation = Constraints.LayoutParams.VERTICAL
                        guidePercent = ((1.0 / vertical) * i).toFloat()
                    }
            })

            constraintLayout.addView(verticalGuide[i])

            constraintLayout.addView(View(constraintLayout.context).apply {
                layoutParams =
                    ConstraintLayout.LayoutParams(5, ConstraintLayout.LayoutParams.MATCH_PARENT)
                        .apply {
                            startToStart = verticalGuide.last().id
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                id = View.generateViewId()
                //setBackgroundColor(Color.BLUE) // 빨간색 선 표시
            })
        }


        for (i in 0..horizontal) {
            horizontalGuide.add(Guideline(constraintLayout.context).apply {
                id = Guideline.generateViewId()
                layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT
                )
                    .apply {
                        orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                        guidePercent = ((1.0 / horizontal) * i).toFloat()
                    }
            })

            constraintLayout.addView(horizontalGuide[i])

            constraintLayout.addView(View(constraintLayout.context).apply {
                layoutParams =
                    ConstraintLayout.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, 5)
                        .apply {
                            leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID  // 왼쪽을 부모에 맞춤
                            rightToRight =
                                ConstraintLayout.LayoutParams.PARENT_ID // 오른쪽을 부모에 맞춤
                            topToTop = horizontalGuide[i].id
                        }
                id = View.generateViewId()
                //setBackgroundColor(Color.BLACK) // 검은색 선 표시
            })
        }


        Log.d("CUSTOM_CALENDAR", "createGuideLine: ${Pair(verticalGuide, horizontalGuide)}")
        return Pair(verticalGuide, horizontalGuide);
    }
}