package org.example;

import java.util.Objects;
import java.util.Scanner;

enum GameState {
    RUNNING, COMPLETED
}

enum GameVerdict {
    WIN, DRAW
}

enum Symbol {
    X, O
}

record Player(int id, String name, Symbol symbol) { }

class Cell {

    Symbol symbol = null;
    public Symbol getSymbol() {
        return symbol;
    }

    public boolean setSymbol(Symbol symbol) {
        if(!isEmpty()) return false;
        this.symbol = symbol;
        return true;
    }

    public boolean isEmpty() {
        return symbol == null;
    }

    @Override
    public String toString() {
        if(symbol == null) return " ";
        return symbol.toString();
    }
}

class Board {

    private final int n;
    private final Cell[][] cells;

    Board(int n) {
        this.n = n;
        cells = new Cell[n][n];
        initialize();
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public boolean makeMove(Player p, Cell cell) {
        return cell.setSymbol(p.symbol());
    }

    public boolean checkRow(int row) {
        Symbol s = cells[row][0].getSymbol();
        if(s == null) return false;
        for (int j = 0; j < n; j++) {
            Cell cell = cells[row][j];
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    public boolean checkCol(int col) {
        Symbol s = cells[0][col].getSymbol();
        if(s == null) return false;
        for (int i = 0; i < n; i++) {
            Cell cell = cells[i][col];
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    public boolean checkDiag() {
        Symbol s = cells[0][0].getSymbol();
        if(s == null) return false;
        for(int k = 0; k < n; k++) {
            Cell cell = cells[k][k];
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    public boolean checkAntiDiag() {
        Symbol s = cells[0][n - 1].getSymbol();
        if(s == null) return false;
        for(int k = 0; k < n; k++) {
            Cell cell = cells[k][n - k - 1];
            if(cell.getSymbol() != s) return false;
        }
        return true;
    }

    private void initialize() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell();
            }
        }
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
}

public class TicTacToeGame {

    private final int n;
    private final int nPlayers;

    private Board board;
    private Player[] players;
    private Player winner;
    private GameVerdict verdict;
    private int curTurn;
    private GameState state;
    private int remMoves;

    TicTacToeGame(int boardSize, int nPlayers) {
        this.n = boardSize;
        this.nPlayers = nPlayers;
    }

    private void initialize() {
        board = new Board(n);

        players = new Player[nPlayers];
        distributeSymbols();

        curTurn = 0;
        winner = null;
        state = GameState.RUNNING;
        remMoves = n * n;
    }

    public void displayBoard() {
        board.display();
    }

    public void start() {
        initialize();
        Scanner sc = new Scanner(System.in);
        while (state != GameState.COMPLETED) {
            System.out.println("Player turn: " + curTurn);
            displayBoard();
            System.out.println("Select cell: ");
            int i = sc.nextInt();
            int j = sc.nextInt();
            while(!board.makeMove(players[curTurn], board.getCell(i, j))) {
                System.out.println("Invalid move, try again,,,");
                displayBoard();
                i = sc.nextInt();
                j = sc.nextInt();
            }

            boolean isWinner = checkWinner(i, j);

            if(isWinner) {
                displayBoard();
                state = GameState.COMPLETED;
                winner = players[curTurn];
                verdict = GameVerdict.WIN;
            }
            if(--remMoves == 0) {
                state = GameState.COMPLETED;
                winner = null;
                verdict = GameVerdict.DRAW;
            }
            curTurn = 1 - curTurn;
        }

        System.out.println("Game Result: " + verdict.toString());
        if(Objects.nonNull(winner)) {
            System.out.println("Winner: "  + winner);
        }
    }

    private boolean checkWinner(int i, int j) {
        return board.checkRow(i) || board.checkCol(j) || board.checkDiag() || board.checkAntiDiag();
    }

    public void distributeSymbols() {
        Symbol[] symbols = Symbol.values();
        String[] names = {"Player1", "Player2"};
        for (int i = 0; i < names.length; i++) {
            players[i] = new Player(i, names[i], symbols[i]);
        }
    }

    public static void main(String[] args) {

        TicTacToeGame game = new TicTacToeGame(3, 2);
        game.start();
        System.out.println("Hello World!");
    }
}