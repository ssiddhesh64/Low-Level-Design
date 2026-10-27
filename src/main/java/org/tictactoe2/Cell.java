package org.tictactoe2;

public class Cell {

    int x;
    int y;

    Symbol symbol = null;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
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
        if(isEmpty()) return " ";
        return symbol.toString();
    }
}
