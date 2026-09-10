package org.tictactoe;

public class ConnectKStrategy implements WinningStrategy {

    private final int k;

    ConnectKStrategy(int k) {

        if (k <= 0) {
            throw new IllegalArgumentException("K must be greater than 0");
        }
        this.k = k;
    }

    @Override
    public boolean checkWinner(Board board, Move move) {
        Position pos = move.position();
        Symbol s = board.getCell(pos).getSymbol();

        if(s == null) return false;

        int[][] directions = {
                {0, 1},    // horizontal
                {1, 0},    // vertical
                {1, 1},    // diagonal
                {-1, 1}    // anti-diagonal
        };

        for (int[] direction : directions) {
            int rowDelta = direction[0];
            int colDelta = direction[1];

            int count =
                    countConsecutive(board, pos, s, rowDelta, colDelta)
                            + countConsecutive(board, pos, s, -rowDelta, -colDelta)
                            - 1;

            if (count >= k) {
                return true;
            }
        }
        return false;
    }

    private int countConsecutive(Board board, Position pos, Symbol s, int rowDelta, int colDelta) {
        int count = 1;

        int row = pos.row() + rowDelta;
        int col = pos.col() + colDelta;

        int n = board.getSize();

        while (row >= 0 && row < n && col >= 0 && col < n) {

            if (board.getCell(new Position(row, col)).getSymbol() != s) {
                break;
            }

            count++;

            row += rowDelta;
            col += colDelta;
        }

        return count;
    }

}
