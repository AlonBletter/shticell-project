package engine.impl;

import dto.CellDTO;
import engine.Cell;
import engine.Function;

import static engine.impl.function.ExpressionTree.*;

public class CellImpl implements Cell {
    private final String originalValue;
    private int row;
    private String column;

    private CellImpl(String originalValue) {
        this.originalValue = originalValue;
    }

    public static CellImpl createCell(String originalValue) {

        return new CellImpl(originalValue);
    }

    public Function<?> getEffectiveValue() {
        Node node = tokenizeExpression(originalValue);

        return buildFunction(node).evaluate();
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    public CellDTO getCell() {

        return new CellDTO();
    }
}
