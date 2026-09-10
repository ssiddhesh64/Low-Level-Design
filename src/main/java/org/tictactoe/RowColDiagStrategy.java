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
        Cell cell = board.getCell(pos);
        Symbol s = cell.getSymbol();
        if(s == null) return false;
        int n = board.getSize();

        for (int j = 0; j < n; j++) {
            cell = board.getCell(new Position(pos.row(), j));
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    private boolean checkCol(Board board, Position pos) {
        Cell cell = board.getCell(pos);
        Symbol s = cell.getSymbol();

        if(s == null) return false;
        int n = board.getSize();

        for (int i = 0; i < n; i++) {
            cell = board.getCell(new Position(i, pos.col()));
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    private boolean checkDiag(Board board, Position pos) {
        if(pos.row() != pos.col()) return false;

        Cell cell = board.getCell(pos);
        Symbol s = cell.getSymbol();
        if(s == null) return false;
        int n = board.getSize();
        for(int k = 0; k < n; k++) {
            cell = board.getCell(new Position(k, k));
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    private boolean checkAntiDiag(Board board, Position pos) {
        int n = board.getSize();

        if(pos.col() + pos.row() != n - 1) return false;
        Cell cell = board.getCell(pos);
        Symbol s = cell.getSymbol();
        if(s == null) return false;

        for(int k = 0; k < n; k++) {
            cell = board.getCell(new Position(k, n - k - 1));
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }
}
