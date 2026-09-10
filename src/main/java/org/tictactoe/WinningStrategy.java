package org.tictactoe;

public interface WinningStrategy {

    boolean checkWinner(Board board, Move move);
}
