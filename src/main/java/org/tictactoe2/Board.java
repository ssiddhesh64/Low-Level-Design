package org.tictactoe2;

import java.util.Arrays;

public class Board {

    int n;
    Cell[][] cells;

    Board(int n) {
        this.n = n;
        cells = new Cell[n][n];
        initialize_cells();
    }

    private void initialize_cells() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Cell c = new Cell(i, j);
                cells[i][j] = c;
            }
        }
    }

    public boolean setSymbol(int x, int y, Symbol s) {
        if (x < 0 || x >= n || y < 0 || y >= n) {
            return false;
        }

        if (s == null || !cells[x][y].isEmpty()) {
            return false;
        }

        cells[x][y].setSymbol(s);
        return true;
    }

    public int getSize() {
        return n;
    }

    public Symbol getSymbol(int x, int y) {
        return cells[x][y].symbol;
    }

    public boolean hasWinner(int x, int y, Symbol s) {

        if (x < 0 || x >= n || y < 0 || y >= n) {
            return false;
        }

        if (s == null || cells[x][y].symbol != s) {
            return false;
        }

        return checkRow(x, y, s)
        || checkCol(x, y, s)
        || checkDiag(x, y, s)
        || checkAntiDiag(x, y, s);
    }

    private boolean checkAntiDiag(int x, int y, Symbol s) {

        if (x + y != n - 1) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (cells[i][n - 1 - i].symbol != s) {
                return false;
            }
        }

        return true;
    }

    private boolean checkDiag(int x, int y, Symbol s) {

        if (x != y) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (cells[i][i].symbol != s) {
                return false;
            }
        }

        return true;
    }

    private boolean checkCol(int x, int y, Symbol s) {
        for (int i = 0; i < n; i++) {
            if(cells[i][y].symbol != s) return false;
        }

        return true;
    }

    private boolean checkRow(int x, int y, Symbol s) {

        for (int i = 0; i < n; i++) {
            if(cells[x][i].symbol != s) return false;
        }

        return true;
    }

    public void display() {
        System.out.println("===Board===");
        for (int i = 0; i < n; i++) {
            System.out.print("| ");
            for (int j = 0; j < n; j++) {
                System.out.print(cells[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && col >= 0 && row < n && col < n;
    }
}
