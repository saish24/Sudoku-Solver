package com.example.sudokusolver.gameLogic

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectedRow = -1
    private var selectedCol = -1

    init {
        selectedLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    fun updateSelectedCell(i : Int, j : Int) {
        selectedRow = i
        selectedCol = j
        selectedLiveData.postValue(Pair(i, j))
    }
}