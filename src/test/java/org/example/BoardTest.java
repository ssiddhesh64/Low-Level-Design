package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(3);
    }

    @Test
    void boardShouldInitializeAllCells() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertNotNull(board.getCell(i, j));
                assertTrue(board.getCell(i, j).isEmpty());
            }
        }
    }

    @Test
    void shouldMakeValidMove() {
        Player player = new Player(1, "Player1", Symbol.X);
        Cell cell = board.getCell(0, 0);

        boolean result = board.makeMove(player, cell);

        assertTrue(result);
        assertEquals(Symbol.X, cell.getSymbol());
    }

    @Test
    void shouldRejectMoveOnOccupiedCell() {
        Player player1 = new Player(1, "Player1", Symbol.X);
        Player player2 = new Player(2, "Player2", Symbol.O);

        Cell cell = board.getCell(0, 0);

        assertTrue(board.makeMove(player1, cell));
        assertFalse(board.makeMove(player2, cell));

        assertEquals(Symbol.X, cell.getSymbol());
    }

    @Test
    void shouldDetectWinningRow() {
        Player player = new Player(1, "Player1", Symbol.X);

        board.makeMove(player, board.getCell(0, 0));
        board.makeMove(player, board.getCell(0, 1));
        board.makeMove(player, board.getCell(0, 2));

        assertTrue(board.checkRow(0));
    }

    @Test
    void shouldDetectWinningColumn() {
        Player player = new Player(1, "Player1", Symbol.X);

        board.makeMove(player, board.getCell(0, 1));
        board.makeMove(player, board.getCell(1, 1));
        board.makeMove(player, board.getCell(2, 1));

        assertTrue(board.checkCol(1));
    }

    @Test
    void shouldDetectWinningDiagonal() {
        Player player = new Player(1, "Player1", Symbol.X);

        board.makeMove(player, board.getCell(0, 0));
        board.makeMove(player, board.getCell(1, 1));
        board.makeMove(player, board.getCell(2, 2));

        assertTrue(board.checkDiag());
    }

    @Test
    void shouldDetectWinningAntiDiagonal() {
        Player player = new Player(1, "Player1", Symbol.X);

        board.makeMove(player, board.getCell(0, 2));
        board.makeMove(player, board.getCell(1, 1));
        board.makeMove(player, board.getCell(2, 0));

        assertTrue(board.checkAntiDiag());
    }

    @Test
    void shouldNotDetectIncompleteRowAsWinning() {
        Player player = new Player(1, "Player1", Symbol.X);

        board.makeMove(player, board.getCell(0, 0));
        board.makeMove(player, board.getCell(0, 1));

        assertFalse(board.checkRow(0));
    }

    @Test
    void shouldNotDetectEmptyDiagonalAsWinning() {
        assertFalse(board.checkDiag());
        assertFalse(board.checkAntiDiag());
    }
}
