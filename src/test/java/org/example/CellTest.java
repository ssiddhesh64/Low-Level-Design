package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @Test
    void newCellShouldBeEmpty() {
        Cell cell = new Cell();

        assertTrue(cell.isEmpty());
        assertNull(cell.getSymbol());
    }

    @Test
    void shouldPlaceSymbolOnEmptyCell() {
        Cell cell = new Cell();

        boolean result = cell.setSymbol(Symbol.X);

        assertTrue(result);
        assertEquals(Symbol.X, cell.getSymbol());
        assertFalse(cell.isEmpty());
    }

    @Test
    void shouldNotAllowOverwritingSymbol() {
        Cell cell = new Cell();

        cell.setSymbol(Symbol.X);

        boolean result = cell.setSymbol(Symbol.O);

        assertFalse(result);
        assertEquals(Symbol.X, cell.getSymbol());
    }
}
