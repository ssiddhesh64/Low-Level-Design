package org.tictactoe2;

import org.tictactoe2.strategy.ConnectKStrategy;
import org.tictactoe2.strategy.DefaultStrategy;
import org.tictactoe2.strategy.WinningStrategy;

public class TicTacToe {

    public static void main(String[] args) {

        int boardSize = 3;
        int players = 2;

        WinningStrategy strategy = new DefaultStrategy(boardSize);

        WinningStrategy connectKStrategy = new ConnectKStrategy(2);

        TicTacToeGame t = new TicTacToeGame(players, boardSize, connectKStrategy);
        t.start();
    }
}
