package com.example.sudokusolver.view.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.sudokusolver.gameLogic.Cell
import kotlin.math.min

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet){

    private val size = 9
    private val sqSize = 3

    private var cellSizePixels = 0f

    private var selectedRow = -1
    private var selectedCol = -1

    private var listener : OnTouchListener? = null

    private var cells : List<Cell>? = null

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

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 45F
    }

    private val boldTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 50F
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sqSize = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sqSize, sqSize)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (1f * width / size)
        fillCells(canvas)
        drawGrid(canvas)
        drawText(canvas)
    }

    private fun fillCells(canvas: Canvas) {

        cells?.forEach {
            val i = it.row
            val j = it.col
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

    private fun drawText(canvas: Canvas) {
        cells?.forEach {
            val valString: String = it.value.toString()
            val paint = if (it.isStarting) boldTextPaint else textPaint;

            val textBounds = Rect()
            paint.getTextBounds(valString, 0, valString.length, textBounds)
            val textWidth = paint.measureText(valString)
            val textHeight = textBounds.height()
            canvas.drawText(valString,
                (it.col * cellSizePixels) + (cellSizePixels / 2f) - (textWidth / 2f),
                (it.row * cellSizePixels) + (cellSizePixels / 2f) + (textHeight / 2f),
                paint);
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

    fun updateCells(cells : List<Cell>) {
        this.cells = cells
        invalidate()
    }

    interface OnTouchListener {
        fun onTouch(i : Int, j : Int)
    }

    fun setListener(listener: OnTouchListener) {
        this.listener = listener
    }
}