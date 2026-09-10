package org.tictactoe;

public class RowColDiagStrategy implements WinningStrategy {

    @Override
    public boolean checkWinner(Board board, Move move) {
        Position pos = move.position();
        return checkRow(board, pos)
                || checkCol(board, pos)
                || checkDiag(board, pos)
                || checkAntiDiag(board, pos);
    }

    private boolean checkRow(Board board, Position pos) {
        Symbol s = board.getSymbol(pos);
        if(s == null) return false;
        int n = board.getSize();

        for (int j = 0; j < n; j++) {
            if(board.getSymbol(pos.row(), j) != s) return false;
        }
        return true;
    }

    private boolean checkCol(Board board, Position pos) {
        Symbol s = board.getSymbol(pos);

        if(s == null) return false;
        int n = board.getSize();

        for (int i = 0; i < n; i++) {
            if(board.getSymbol(i, pos.col()) != s) return false;
        }
        return true;
    }

    private boolean checkDiag(Board board, Position pos) {
        if(pos.row() != pos.col()) return false;

        Symbol s = board.getSymbol(pos);
        if(s == null) return false;
        int n = board.getSize();
        for(int k = 0; k < n; k++) {
            if(board.getSymbol(k, k) != s) return false;
        }
        return true;
    }

    private boolean checkAntiDiag(Board board, Position pos) {
        int n = board.getSize();

        if(pos.col() + pos.row() != n - 1) return false;
        Symbol s = board.getSymbol(pos);
        if(s == null) return false;

        for(int k = 0; k < n; k++) {
            if(board.getSymbol(k, n - k - 1) != s) return false;
        }
        return true;
    }
}
