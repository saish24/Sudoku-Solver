package com.example.sudokusolver.gameLogic

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>();

    private val ideal = 511 // 2^0 + 2^1 + ... + 2^8 = 2^9 - 1

    private var rowMask : List<Int> = List(9) {511}
    private var colMask : List<Int> = List(9) {511}
    private var boxMask : List<Int> = List(9) {511}

    private var selectedRow = -1
    private var selectedCol = -1

    private val board : Board

    init {
        val cells = List(81) {
            Cell(it/9, it%9, 1 + ((it%15)%9), (it%16 == 0))}
        board = Board(9, cells)

        cellsLiveData.postValue(board.cells)
        selectedLiveData.postValue(Pair(selectedRow, selectedCol))
    }

    fun handleInput(num : Int) {
        if(selectedCol == -1 ||
           selectedRow == -1 ||
           board.cells[selectedRow * 9 + selectedCol].isStarting) return;

        board.getCell(selectedRow, selectedCol).value = num
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(i : Int, j : Int) {
        if(!board.cells[i * 9 + j].isStarting)
        {
            selectedRow = i
            selectedCol = j
            selectedLiveData.postValue(Pair(i, j))
        }
    }

    fun check (): Boolean {
        for (i:Int in 0 until 9) {
            if(rowMask[i] != 0 || colMask[i] != 0 || boxMask[i] != 0) return false
        }
        return true
    }


}