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

public class Average extends UnaryExpression {
    public Average(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        String rangeName = effectiveValue.extractValueWithExpectation(String.class);
        List<Coordinate> cellsInRange = sheet.getRangeCellsCoordinates(rangeName);

        if (rangeName == null || cellsInRange == null) {
            return new EffectiveValueImpl(CellType.ERROR, Double.NaN);
        }

        double result = 0;
        int numberCounter = 0;
        for(Coordinate coordinate : cellsInRange) {
            CellReadActions cell = sheet.getCell(coordinate);
            EffectiveValue cellEffectiveValue = cell.getEffectiveValue();

            if(cellEffectiveValue.cellType() != CellType.NUMERIC) {
                continue;
            }
            numberCounter++;
            result += cellEffectiveValue.extractValueWithExpectation(Double.class);
        }

        if (numberCounter == 0) {
            throw new IllegalArgumentException("The cells in range [" + rangeName + "] doesn't contain any numbers.");
        }

        return new EffectiveValueImpl(CellType.NUMERIC, result/numberCounter);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}