package com.example.sudokusolver.view.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.min

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet){

    private val size = 9
    private val sqSize = 3

    private var cellSizePixels = 0f

    private var selectedRow = -1
    private var selectedCol = -1

    private var listener : onTouchListener? = null

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6EAD3A")
    }

    private val sameRegionCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#EFEDEF")
    }

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 1.5f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sqSize = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sqSize, sqSize)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (1f * width / size).toFloat()
        fillCells(canvas)
        drawGrid(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if(selectedRow == -1) return
        for (i in 0..size)
        for (j in 0..size)
        {
            if(selectedRow == i && selectedCol == j) fillCell(canvas, i, j, selectedCellPaint)
            else if(selectedRow == i || selectedCol == j ||
                   (i / sqSize == selectedRow / sqSize && j / sqSize == selectedCol / sqSize))
                fillCell(canvas, i, j, sameRegionCellPaint)
        }
    }

    private fun fillCell(canvas: Canvas, i : Int, j : Int, paint: Paint) {
        canvas.drawRect(j * cellSizePixels, i * cellSizePixels, (j+1) * cellSizePixels, (i+1) * cellSizePixels, paint)
    }

    private fun drawGrid(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), thickLinePaint)
        for(i in 1 until size)
        {
            val currPaint = when(i % sqSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            canvas.drawLine(i * cellSizePixels, 0f, i * cellSizePixels, height.toFloat(), currPaint)
            canvas.drawLine(0f, i * cellSizePixels, width.toFloat(), i * cellSizePixels,  currPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x : Float, y : Float) {
        val nextselectedRow = (y / cellSizePixels).toInt()
        val nextselectedCol = (x / cellSizePixels).toInt()
        listener?.onTouch(nextselectedRow, nextselectedCol)
    }

    fun updateSelectedCellUI(i : Int, j : Int) {
        selectedRow = i
        selectedCol = j
        invalidate()
    }

    interface onTouchListener {
        fun onTouch(i : Int, j : Int)
    }

    fun setListener(listener: onTouchListener) {
        this.listener = listener
    }
}