package org.tictactoe2;

import org.tictactoe2.strategy.WinningStrategy;

import java.util.Scanner;

public class TicTacToeGame {

    private Status gameStatus;
    private Board board;
    private final int boardSize;
    private final int nPlayers;
    private int turn;
    private int moves;
    Player[] players;

    private final WinningStrategy winningStrategy;

    Scanner sc = new Scanner(System.in);

    TicTacToeGame(int nPlayers, int boardSize, WinningStrategy strategy) {

        if (boardSize <= 0) {
            throw new IllegalArgumentException(
                    "Board size must be positive."
            );
        }

        if (nPlayers < 2 || nPlayers > Symbol.values().length) {
            throw new IllegalArgumentException(
                    "Unsupported number of players."
            );
        }

        this.nPlayers = nPlayers;
        this.boardSize = boardSize;
        this.players = new Player[nPlayers];

        this.winningStrategy = strategy;
    }

    void initialize() {
        this.board = new Board(boardSize);
        this.players = new Player[nPlayers];
        this.turn = 0;
        this.moves = 0;
        assignSymbols();
        this.gameStatus = Status.IN_PROGRESS;
    }

    private void assignSymbols() {

        Symbol[] values = Symbol.values();
        for (int i = 0; i < nPlayers; i++) {
            players[i] = new Player(i, values[i]);
        }
    }

    void start() {
        initialize();
        while(gameStatus == Status.IN_PROGRESS) {
            System.out.println("Player " + turn + " turn. Select x and y: ");
            board.display();

            String input = sc.nextLine().trim();
            String[] coordinates = input.split("\\s+");

            if (coordinates.length != 2) {
                System.out.println("Enter exactly two coordinates: x y");
                continue;
            }

            int x;
            int y;

            try {
                x = Integer.parseInt(coordinates[0]);
                y = Integer.parseInt(coordinates[1]);
            } catch (NumberFormatException e) {
                System.out.println("Coordinates must be integers.");
                continue;
            }

            if(!board.setSymbol(x, y, players[turn].symbol())) {
                System.out.println("Invalid move. Try Again!!");
                continue;
            }

            moves++;

            if(winningStrategy.checkWinner(board, x, y, players[turn].symbol())) {
                System.out.println("Game Completed. Winner: " + players[turn]);
                gameStatus = Status.COMPLETE;
            } else if(moves == boardSize * boardSize) {
                System.out.println("Game ended in a Draw!!");
                gameStatus = Status.COMPLETE;
            }

            turn = (turn + 1) % nPlayers;
        }
    }
}
