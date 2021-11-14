package com.example.sudokusolver.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudokusolver.R
import com.example.sudokusolver.view.customViews.SudokuBoardView
import com.example.sudokusolver.viewModel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.onTouchListener {
    private lateinit var playSudokuViewModel: PlaySudokuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        sudokuBoardView.setListener(this)

        playSudokuViewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)
        playSudokuViewModel.sudokuGame.selectedLiveData.observe(this, Observer { updateSelectedCell(it) })
    }

    private fun updateSelectedCell(cell : Pair<Int, Int> ?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onTouch(i: Int, j: Int) {
        playSudokuViewModel.sudokuGame.updateSelectedCell(i, j)
    }
}