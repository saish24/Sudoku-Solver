package com.example.sudokusolver.gameLogic

import android.util.Log
import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var disableCellLiveData = MutableLiveData<List<Int>>()
    var changeDifficultyLiveData = MutableLiveData<Int>()

    var easy = 0
    var medium = 0
    var hard = 0

    private val ideal = 1022 // 2^1 + 2^2 + ... + 2^9 = (2^10 - 1) - (2^0) = 1022

    private var rowMask = MutableList(9) {ideal}
    private var colMask = MutableList(9) {ideal}
    private var boxMask = MutableList(3) { MutableList(3) {ideal} }

    private var selectedRow = -1
    private var selectedCol = -1

    private val board : Board = Board(9, MutableList<Cell>(81) {Cell(it/9, it%9, 0, false)})

    private val easyGames : List<List<Int>> = listOf(
        listOf(0, 1, 0, 0, 0, 0, 0, 8, 9, 0, 0, 5, 1, 0, 0, 6, 0, 0, 9, 0, 6, 4, 0, 3, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0, 9, 0, 0, 0, 0, 8, 3, 6, 0, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0, 0, 0, 8, 0, 6, 0, 2, 7, 0, 4, 0, 0, 7, 0, 0, 4, 2, 0, 0, 2, 4, 0, 0, 0, 0, 0, 6, 0),
        listOf(0, 0, 0, 0, 8, 5, 0, 3, 6, 7, 1, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 2, 0, 5, 0, 8, 0, 0, 0, 5, 0, 1, 0, 0, 6, 0, 3, 7, 2, 0, 0, 0, 9, 1, 0, 2, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 6, 0, 0, 8, 0, 0, 9, 0, 3, 0, 0, 0, 7, 4, 0, 0, 9, 0, 0, 0, 6, 0),
        listOf(0, 0, 8, 0, 0, 3, 2, 0, 5, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 7, 0, 6, 0, 0, 0, 4, 0, 0, 5, 4, 0, 0, 9, 0, 3, 0, 2, 8, 0, 0, 0, 0, 7, 0, 6, 0, 9, 0, 7, 0, 1, 0, 0, 0, 0, 0, 6, 0, 1, 0, 0, 0, 0, 0, 0, 0, 5, 3, 0, 0, 0, 9, 1, 0, 0, 2, 7, 0, 5, 0, 0),
        listOf(9, 0, 0, 3, 0, 0, 0, 0, 7, 0, 7, 6, 1, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 5, 8, 0, 0, 3, 0, 9, 0, 0, 0, 0, 0, 9, 5, 0, 4, 2, 0, 8, 1, 0, 0, 2, 8, 0, 0, 0, 6, 0, 1, 0, 0, 0, 0, 6, 7, 4, 0, 0, 0, 0, 0, 0, 5, 0, 0, 3, 5, 8, 0, 0, 0, 7, 2, 1, 0),
        listOf(0, 0, 0, 0, 1, 8, 0, 0, 5, 3, 2, 0, 0, 0, 0, 0, 7, 0, 4, 0, 0, 7, 0, 0, 0, 6, 3, 8, 1, 0, 4, 0, 5, 0, 0, 2, 6, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 7, 1, 9, 0, 0, 0, 0, 0, 0, 9, 2, 0, 6, 0, 1, 0, 0, 0, 0, 0, 4, 0, 9, 0, 8, 5, 4, 0, 0, 3, 0, 0, 0, 0)
    )
    private val medGames : List<List<Int>> = listOf(
        listOf(8, 0, 6, 0, 7, 1, 0, 0, 0, 0, 0, 0, 0, 5, 9, 0, 0, 0, 5, 0, 9, 0, 0, 8, 0, 0, 0, 0, 0, 3, 0, 0, 6, 7, 0, 9, 4, 0, 0, 0, 9, 0, 0, 0, 6, 0, 0, 0, 0, 3, 7, 2, 0, 4, 0, 4, 0, 7, 8, 0, 0, 6, 0, 0, 6, 5, 0, 0, 0, 0, 4, 1, 0, 0, 2, 0, 0, 0, 0, 0, 7),
        listOf(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 2, 4, 0, 0, 0, 0, 7, 8, 0, 0, 0, 0, 5, 0, 0, 0, 4, 2, 0, 0, 4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 2, 0, 0, 7, 0, 8, 6, 0, 0, 0, 0, 0, 0, 4, 0, 0, 1, 6, 9, 5, 7, 0, 0, 0, 9, 3, 0, 8, 0, 1, 0, 0, 0, 0, 4, 7, 0, 0, 6),
        listOf(0, 0, 0, 1, 0, 0, 3, 5, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 7, 0, 0, 2, 4, 0, 0, 4, 0, 0, 0, 0, 0, 0, 3, 0, 0, 2, 8, 0, 4, 0, 0, 0, 9, 7, 6, 1, 0, 2, 0, 5, 4, 3, 1, 0, 0, 0, 0, 0, 2, 6, 0, 0, 9, 2, 0, 8, 0, 3, 9, 0, 2, 7, 0, 0, 0, 0, 0),
        listOf(2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 0, 0, 0, 6, 8, 0, 6, 0, 0, 1, 0, 0, 0, 0, 0, 3, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 2, 0, 0, 9, 7, 3, 1, 2, 4, 5, 0, 0, 2, 0, 0, 7, 0, 9, 0, 0, 0, 8, 0, 0, 0, 6, 3, 0, 5, 9, 0, 0, 0, 3, 0, 8, 0, 2),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 7, 2, 0, 0, 0, 0, 0, 0, 7, 5, 0, 6, 0, 0, 5, 6, 3, 0, 9, 1, 0, 0, 0, 0, 0, 6, 1, 0, 0, 0, 5, 6, 2, 1, 0, 9, 0, 7, 8, 0, 8, 0, 0, 0, 2, 1, 5, 9, 0, 0, 7, 0, 8, 3, 6, 4, 1, 0)
    )
    private val hardGames : List<List<Int>> = listOf(
        listOf(0, 0, 0, 0, 0, 0, 6, 0, 0, 2, 3, 0, 0, 6, 0, 0, 0, 9, 5, 6, 8, 0, 0, 9, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 8, 0, 7, 0, 0, 1, 0, 0, 9, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 7, 5, 9, 0, 4, 0, 0, 0, 8, 9, 0, 0, 5, 0, 7, 6, 4),
        listOf(0, 0, 4, 9, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 5, 0, 9, 0, 0, 9, 0, 5, 0, 0, 0, 0, 0, 0, 0, 3, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 5, 0, 2, 0, 0, 0, 0, 0, 0, 9, 0, 0, 7, 3, 1, 4, 5, 0),
        listOf(9, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 5, 0, 0, 5, 0, 4, 0, 6, 9, 0, 3, 0, 6, 7, 0, 0, 0, 5, 8, 0, 8, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 9, 0, 2, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 3, 5, 0, 4),
        listOf(0, 6, 0, 0, 0, 3, 0, 5, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 5, 6, 0, 0, 3, 0, 0, 3, 0, 0, 0, 6, 9, 0, 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 3, 0, 0, 0, 0, 5, 0, 0, 2, 0, 0, 0, 0, 0, 0, 6, 0, 0, 7, 1, 0, 0, 3, 0, 0, 0, 0, 8, 0, 0, 0, 1, 6),
        listOf(0, 0, 0, 0, 2, 8, 0, 0, 0, 0, 0, 3, 0, 0, 0, 5, 8, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 2, 1, 0, 3, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 9, 6, 0, 0, 0, 0, 3, 5, 0, 0, 0, 7, 8, 0, 0, 0, 0, 0, 0, 0, 9, 0, 3, 0, 2, 1, 9, 0, 0, 6, 0, 5, 0, 7, 3)
    )

    init { changeDifficulty(1) }

    fun handleInput(num : Int) {
        if(selectedCol == -1 ||
           selectedRow == -1 ||
           board.cells[selectedRow * 9 + selectedCol].isStarting) return

        unSetBits(selectedRow, selectedCol, board.cells[selectedRow * 9 + selectedCol].value)
        setBits(selectedRow, selectedCol, num)

        board.getCell(selectedRow, selectedCol).value = num

        disableCellLiveData.postValue(putDisabled(selectedRow, selectedCol))
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
            if(((1 shl x) and possible) != 0) res.add(x)
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
                            cellsLiveData.postValue(board.cells)

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
            disableCellLiveData.postValue(putDisabled(selectedRow, selectedCol))
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

        disableCellLiveData.postValue(listOf(1,2,3,4,5,6,7,8,9))
        selectedLiveData.postValue(Pair(-1, -1))
        cellsLiveData.postValue(board.cells)
    }

    fun changeDifficulty(j : Int) {
        var i = 0
        when(j) {
            1 -> {
                easy = (easy + 1) % easyGames.size
                board.cells.forEach {
                    unSetBits(it.row, it.col, board.getCell(it.row, it.col).value)
                    it.value = easyGames[easy][i++]
                    it.isStarting = it.value > 0
                    setBits(it.row, it.col, board.getCell(it.row, it.col).value)
                }
            }
            2 -> {
                medium = (medium + 1) % medGames.size
                board.cells.forEach {
                    unSetBits(it.row, it.col, board.getCell(it.row, it.col).value)
                    it.value = medGames[medium][i++]
                    it.isStarting = it.value > 0
                    setBits(it.row, it.col, board.getCell(it.row, it.col).value)
                }
            }
            3 -> {
                hard = (hard + 1) % hardGames.size
                board.cells.forEach {
                    unSetBits(it.row, it.col, board.getCell(it.row, it.col).value)
                    it.value = hardGames[hard][i++]
                    it.isStarting = it.value > 0
                    setBits(it.row, it.col, board.getCell(it.row, it.col).value)
                }
            }
        }

        selectedLiveData.postValue(Pair(-1, -1))
        cellsLiveData.postValue(board.cells)
    }
}