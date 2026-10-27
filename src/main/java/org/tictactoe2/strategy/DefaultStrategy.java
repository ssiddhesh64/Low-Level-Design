package org.tictactoe2.strategy;

import org.tictactoe2.Board;
import org.tictactoe2.Symbol;

public class DefaultStrategy implements WinningStrategy {

    int n;
    public DefaultStrategy(int n) {
        this.n = n;
    }

    @Override
    public boolean checkWinner(Board board, int x, int y, Symbol s) {

        int n = board.getSize();

        if (!board.isValidPosition(x, y)) {
            return false;
        }

        if (s == null || board.getSymbol(x, y) != s) {
            return false;
        }

        return checkRow(board, x, s)
                || checkCol(board, y, s)
                || checkDiag(board, x, y, s)
                || checkAntiDiag(board, x, y, s);
    }

    private boolean checkAntiDiag(Board board, int x, int y, Symbol s) {

        if (x + y != n - 1) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (board.getSymbol(i, n - 1 - i) != s) {
                return false;
            }
        }

        return true;
    }

    private boolean checkDiag(Board board, int x, int y, Symbol s) {

        if (x != y) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (board.getSymbol(i, i) != s) {
                return false;
            }
        }

        return true;
    }

    private boolean checkCol(Board board, int col, Symbol s) {
        for (int i = 0; i < n; i++) {
            if(board.getSymbol(i, col) != s) return false;
        }

        return true;
    }

    private boolean checkRow(Board board, int row, Symbol s) {

        for (int i = 0; i < n; i++) {
            if(board.getSymbol(row, i) != s) return false;
        }

        return true;
    }
}
