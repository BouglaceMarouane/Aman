package com.example.aman.ui.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.aman.R

class OnboardingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var dotCount = 3
    private var currentPosition = 0

    private val dotRadius = 8f
    private val selectedDotWidth = 48f
    private val dotSpacing = 16f

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val selectedColor = ContextCompat.getColor(context, R.color.blue_primary)
    private val unselectedColor = ContextCompat.getColor(context, R.color.indicator_unselected)

    private var animatedPosition = 0f
    private var animator: ValueAnimator? = null

    init {
        setWillNotDraw(false)
    }

    fun setDotCount(count: Int) {
        dotCount = count
        invalidate()
    }

    fun setCurrentPosition(position: Int) {
        animator?.cancel()

        animator = ValueAnimator.ofFloat(currentPosition.toFloat(), position.toFloat()).apply {
            duration = 300
            addUpdateListener { animation ->
                animatedPosition = animation.animatedValue as Float
                invalidate()
            }
            start()
        }

        currentPosition = position
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = ((dotCount - 1) * (dotRadius * 2 + dotSpacing) + selectedDotWidth).toInt()
        val height = (dotRadius * 2 + 16).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        var startX = 0f

        for (i in 0 until dotCount) {
            val isSelected = i == currentPosition
            val progress = 1f - Math.abs(animatedPosition - i).coerceIn(0f, 1f)

            if (isSelected) {
                // Draw selected indicator (rounded rectangle)
                dotPaint.color = selectedColor
                val currentWidth = dotRadius * 2 + (selectedDotWidth - dotRadius * 2) * progress

                canvas.drawRoundRect(
                    startX,
                    centerY - dotRadius,
                    startX + currentWidth,
                    centerY + dotRadius,
                    dotRadius,
                    dotRadius,
                    dotPaint
                )
                startX += currentWidth + dotSpacing
            } else {
                // Draw unselected dot (circle)
                val colorProgress = 1f - progress
                dotPaint.color = ArgbEvaluator().evaluate(
                    colorProgress,
                    selectedColor,
                    unselectedColor
                ) as Int

                canvas.drawCircle(
                    startX + dotRadius,
                    centerY,
                    dotRadius,
                    dotPaint
                )
                startX += dotRadius * 2 + dotSpacing
            }
        }
    }
}