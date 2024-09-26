package engine.expression.impl.system;

import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

public class Ref extends UnaryExpression {
    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        String arg = effectiveValue.extractValueWithExpectation(String.class);

        if (arg == null) {
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() + " function!\n" +
                    "Expected <"+ CellType.TEXT +"> but received <" + effectiveValue.cellType() + ">");
        }

        Coordinate coordinate = CoordinateFactory.createCoordinate(arg);
        CellReadActions cell = sheet.getCell(coordinate);

        return cell.getEffectiveValue();
    }

    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
