package com.example.sudokusolver.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.example.sudokusolver.R
import com.example.sudokusolver.gameLogic.Cell
import com.example.sudokusolver.view.customViews.SudokuBoardView
import com.example.sudokusolver.viewModel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {
    private lateinit var playSudokuViewModel: PlaySudokuViewModel
    private lateinit var buttonList : List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        sudokuBoardView.setListener(this)

        playSudokuViewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)

        playSudokuViewModel.sudokuGame.selectedLiveData.observe( this, { updateSelectedCell(it) })
        playSudokuViewModel.sudokuGame.cellsLiveData.observe(this, { updateCells(it) })
        playSudokuViewModel.sudokuGame.disableCellLiveData.observe(this, { disableCells(it) })

        buttonList = listOf(button1, button2, button3, button4, button5, button6, button7, button8, button9)

        buttonList.forEachIndexed { index, button ->
            button.setOnClickListener { playSudokuViewModel.sudokuGame.handleInput(index + 1) }
        }

        clear.setOnClickListener {
            playSudokuViewModel.sudokuGame.clearInput()
        }

        solve.setOnClickListener {
            playSudokuViewModel.sudokuGame.solve()
        }
    }

    private fun updateCells(cells: List<Cell>) {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCell(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onTouch(i: Int, j: Int) {
        playSudokuViewModel.sudokuGame.updateSelectedCell(i, j)
    }

    override fun disableCells(listCells: List<Int>) {
        buttonList.forEachIndexed { index, button ->
            button.setTextColor(if(listCells.contains(index+1)) Color.WHITE else Color.BLACK)
            button.isClickable = listCells.contains(index+1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sudoku, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.mi_refresh -> playSudokuViewModel.sudokuGame.restart()
        }
        return true
    }
}