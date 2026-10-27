package org.tictactoe2.strategy;

import org.tictactoe2.Board;
import org.tictactoe2.Symbol;

public class ConnectKStrategy implements WinningStrategy {

    private final int k;

    public ConnectKStrategy(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }

        this.k = k;
    }

    @Override
    public boolean checkWinner(Board board, int x, int y, Symbol symbol) {

        int[][] directions = {
                {0, 1},   // Horizontal
                {1, 0},   // Vertical
                {1, 1},   // Main diagonal
                {1, -1}   // Anti-diagonal
        };

        for (int[] direction : directions) {
            int count = 1;

            count += countConsecutive(
                    board, x, y,
                    direction[0], direction[1], symbol
            );

            count += countConsecutive(
                    board, x, y,
                    -direction[0], -direction[1], symbol
            );

            if (count >= k) {
                return true;
            }
        }

        return false;
    }

    private int countConsecutive(
            Board board,
            int x,
            int y,
            int dx,
            int dy,
            Symbol symbol
    ) {
        int count = 0;

        int row = x + dx;
        int col = y + dy;

        while (board.isValidPosition(row, col)
                && board.getSymbol(row, col) == symbol) {

            count++;
            row += dx;
            col += dy;
        }

        return count;
    }
}