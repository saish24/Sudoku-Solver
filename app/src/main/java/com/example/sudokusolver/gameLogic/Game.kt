package com.example.sudokusolver.gameLogic

enum class Game {
    EASY, MEDIUM, HARD;

    val easyGames : List<List<Int>> = listOf(
        listOf(0, 1, 0, 0, 0, 0, 0, 8, 9, 0, 0, 5, 1, 0, 0, 6, 0, 0, 9, 0, 6, 4, 0, 3, 0, 1, 0, 0, 0, 4, 0, 0, 0, 0, 9, 0, 0, 0, 0, 8, 3, 6, 0, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0, 0, 0, 8, 0, 6, 0, 2, 7, 0, 4, 0, 0, 7, 0, 0, 4, 2, 0, 0, 2, 4, 0, 0, 0, 0, 0, 6, 0),
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        listOf()
    )
}