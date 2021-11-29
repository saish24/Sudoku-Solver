package com.example.sudokusolver.gameLogic

import android.util.Log
import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>();
    var disableCellLiveData = MutableLiveData<List<Int>>();

    var difficulty = Game.EASY

    private val ideal = 1022 // 2^1 + 2^2 + ... + 2^9 = (2^10 - 1) - (2^0) = 1022

    private var rowMask = MutableList(9) {ideal}
    private var colMask = MutableList(9) {ideal}
    private var boxMask = MutableList(3) { MutableList(3) {ideal} }

    private var selectedRow = -1
    private var selectedCol = -1

    private val board : Board

    init {
        val cells = MutableList(81) {Cell(it/9, it%9, 0, false)}
        board = Board(9, cells)

        difficulty = Game.EASY

        // make above array with a network call to bring in sudoku puzzle

        cells.forEach {
//            Log.d("SUDOKU", "$filter")

            if(it.isStarting && it.value != 0)
                setBits(it.row, it.col, it.value)
        }

        disableCellLiveData.postValue(emptyList())
        cellsLiveData.postValue(board.cells)
    }

    fun handleInput(num : Int) {
        if(selectedCol == -1 ||
           selectedRow == -1 ||
           board.cells[selectedRow * 9 + selectedCol].isStarting) return;

        setBits(selectedRow, selectedCol, num)
        board.getCell(selectedRow, selectedCol).value = num

        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(i : Int, j : Int) {
        if(i != -1 && j != -1 && !board.cells[i * 9 + j].isStarting)
        {
            selectedRow = i
            selectedCol = j
            disableCellLiveData.postValue(putDisabled(selectedRow, selectedCol))
            selectedLiveData.postValue(Pair(i, j))
        }
    }

    private fun putDisabled(i : Int, j : Int): MutableList<Int> {
        val res : MutableList<Int> = emptyList<Int>().toMutableList()

        val possible : Int = rowMask[i] and colMask[j] and boxMask[i/3][j/3]

        Log.d("SUDOKU", "$possible - $i $j")

        for (x in  1..9)
        {
            if(((1 shl(x)) and possible) != 0) res.add(x)
        }

        return res
    }

    private fun recurse() : Boolean {
        for(i in 0 until 9) {
            for(j in 0 until 9) {
                if(board.cells[i * 9 + j].value == 0) {

                    val possible : Int = rowMask[i] and colMask[j] and boxMask[i/3][j/3]
                    if(possible == 0) return false

                    for (x in  1..9) {
                        val mask = (1 shl (x)) and possible
                        if(mask != 0) {
                            board.cells[i*9 + j].value = x
                            setBits(i, j, x)
//                            cellsLiveData.postValue(board.cells)

                            if(recurse()) return true
                            unSetBits(i, j, x)
                        }
                    }

                    board.cells[i * 9 + j].value = 0
                    return false
                }
            }
        }

        cellsLiveData.postValue(board.cells)
        return true
    }

    fun solve() {
        if(recurse()) Log.d("SUDOKU", "#solved")
        else Log.d("SUDOKU", "#help")
    }

    fun clearInput() {
        if(selectedRow != -1 && selectedCol != -1) {
            unSetBits(selectedRow, selectedCol, board.getCell(selectedRow, selectedCol).value)
            board.getCell(selectedRow, selectedCol).value = 0
            cellsLiveData.postValue(board.cells)
        }
    }


    private fun setBits(i : Int, j : Int, num : Int) {

        val filter : Int = if(num != 0) ideal xor (1 shl num) else ideal

        rowMask[i] = rowMask[i] and filter
        colMask[j] = colMask[j] and filter
        boxMask[i/3][j/3] = boxMask[i/3][j/3] and filter

        Log.d("SUDOKU", "SET BITS - ${rowMask[i]} ${colMask[j]} ${boxMask[i/3][j/3]}")
    }

    private fun unSetBits(i : Int, j : Int, num : Int) {
        val filter : Int = if(num != 0) ideal and (1 shl num) else 0

        rowMask[i] = rowMask[i] or filter
        colMask[j] = colMask[j] or filter
        boxMask[i/3][j/3] = boxMask[i/3][j/3] or filter

        Log.d("SUDOKU", "UNSET BITS - ${rowMask[i]} ${colMask[j]} ${boxMask[i/3][j/3]}")
    }

    fun restart() {
        board.cells.forEach {
            if(!it.isStarting) {
                unSetBits(it.row, it.col, it.value)
                it.value = 0
            }
        }

        // TODO : correct this error! ⚠
        disableCellLiveData.postValue(emptyList())

        cellsLiveData.postValue(board.cells)
    }
}