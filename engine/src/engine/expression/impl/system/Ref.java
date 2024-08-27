package engine.expression.impl.system;

import dto.CellDTO;
import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.EffectiveValueImpl;

public class Ref extends UnaryExpression {
    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        String arg = effectiveValue.extractValueWithExpectation(String.class);

        // Aviad needs to decide what to present if there is a ref to an empty cell \ invalid argument.
        if (arg == null) {
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() + " function!\n" +
                    "Expected <"+ CellType.TEXT +"> but received <" + effectiveValue.getCellType() + ">");
        }

        Coordinate coordinate = CoordinateFactory.createCoordinate(arg);
        CellReadActions cell = sheet.getCell(coordinate);


//        if(cell == null) { celltype empty
//            return new EffectiveValueImpl(CellType.TEXT, "!UNDEFINED!");
//        }

        return cell.getEffectiveValue();
    }

    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
