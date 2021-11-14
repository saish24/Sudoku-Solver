package com.example.sudokusolver.viewModel

import androidx.lifecycle.ViewModel
import com.example.sudokusolver.gameLogic.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame : SudokuGame = SudokuGame()
}