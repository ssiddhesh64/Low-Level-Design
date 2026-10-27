package org.tictactoe2.strategy;

import org.tictactoe2.Board;
import org.tictactoe2.Symbol;

public interface WinningStrategy {

    boolean checkWinner(Board board, int x, int y, Symbol s);
}
