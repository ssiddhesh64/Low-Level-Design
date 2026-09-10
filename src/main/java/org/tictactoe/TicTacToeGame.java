package org.tictactoe;

import java.util.List;
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

record Position(int row, int col) { }

record Move(Player player, Position position) { }

class Cell {

//    private final Position position;
//
//    public Cell(Position position) {
//        this.position = position;
//    }

    private Symbol symbol = null;
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

    public int getSize() {
        return n;
    }

    Board(int n) {
        this.n = n;
        cells = new Cell[n][n];
        initialize();
    }

    public Cell getCell(Position pos) {
        return cells[pos.row()][pos.col()];
    }

    public boolean makeMove(Move move) {
        Position pos = move.position();
        if(!isValid(pos)) return false;

        Cell cell = getCell(pos);
        return cell.setSymbol(move.player().symbol());
    }

    private boolean isValid(Position pos) {
        int row = pos.row();
        int col = pos.col();
        return row >= 0 && col >= 0 && row < n && col < n;
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

    private Board board;
    private final List<Player> players;

    private Player winner;
    private GameVerdict verdict;
    private GameState state;

    private int curTurn;
    private int remMoves;

    private final WinningStrategy winningStrategy;

    TicTacToeGame(int boardSize, List<Player> players, WinningStrategy winningStrategy) {
        this.n = boardSize;

        this.players = List.copyOf(players);

        if (players.size() != 2) {
            throw new IllegalArgumentException("Tic-Tac-Toe requires exactly 2 players");
        }

        this.winningStrategy = winningStrategy;
        initialize();
    }

    public GameState getState() {
        return state;
    }

    public GameVerdict getVerdict() {
        return verdict;
    }

    public Player getWinner() {
        return winner;
    }

    private void initialize() {
        board = new Board(n);

        curTurn = 0;
        winner = null;
        state = GameState.RUNNING;
        remMoves = n * n;
    }

    public void displayBoard() {
        board.display();
    }

    public void start() {

        Scanner sc = new Scanner(System.in);
        while (state == GameState.RUNNING) {
            displayBoard();

            System.out.println("Player turn: " + curTurn);
            System.out.println("Select cell: ");

            int i = sc.nextInt();
            int j = sc.nextInt();

            while(!makeMove(new Position(i, j))) {
                System.out.println("Invalid move, try again,,,");
                i = sc.nextInt();
                j = sc.nextInt();
            }
        }
        displayBoard();
        displayResult();
    }

    private void displayResult() {
        System.out.println("Game Result: " + verdict.toString());
        if(Objects.nonNull(winner)) {
            System.out.println("Winner: "  + winner);
        }
    }

    public boolean makeMove(Position position) {

        if (state != GameState.RUNNING) return false;

        Player curPlayer = players.get(curTurn);
        Move move = new Move(curPlayer, position);

        if(!board.makeMove(move)) return false;

        boolean isWinner = winningStrategy.checkWinner(board, move);

        if(isWinner) {
            state = GameState.COMPLETED;
            winner = curPlayer;
            verdict = GameVerdict.WIN;
        } else if(--remMoves == 0) {
            state = GameState.COMPLETED;
            winner = null;
            verdict = GameVerdict.DRAW;
        } else {
            nextTurn();
        }
        return true;
    }

    private void nextTurn() {
        curTurn = (curTurn + 1) % players.size();
    }

    public static void main(String[] args) {

        List<Player> players = List.of(
                new Player(1, "Player1", Symbol.X),
                new Player(2, "Player2", Symbol.O)
        );

        TicTacToeGame game =
                new TicTacToeGame(3, players, new RowColDiagStrategy());
        game.start();

        TicTacToeGame game2 =
                new TicTacToeGame(3, players, new ConnectKStrategy(2));
        game2.start();
    }
}