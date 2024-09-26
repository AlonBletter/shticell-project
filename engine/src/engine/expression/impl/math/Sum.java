package engine.expression.impl.math;

import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

import java.util.List;

public class Sum extends UnaryExpression {
    public Sum(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        String rangeName = effectiveValue.extractValueWithExpectation(String.class);
        List<Coordinate> cellsInRange = sheet.getRangeCellsCoordinates(rangeName);

        if (rangeName == null) {
            return new EffectiveValueImpl(CellType.ERROR, Double.NaN);
        }

        double result = 0;
        for(Coordinate coordinate : cellsInRange) {
            CellReadActions cell = sheet.getCell(coordinate);
            EffectiveValue cellEffectiveValue = cell.getEffectiveValue();

            if(cellEffectiveValue.cellType() != CellType.NUMERIC) {
                result = 0;
                continue;
            }
            result += cellEffectiveValue.extractValueWithExpectation(Double.class);
        }

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}