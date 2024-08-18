package engine.expression.impl.system;

import dto.CellDTO;
import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.EffectiveValueImpl;

public class Ref extends UnaryExpression {
    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        Coordinate coordinate = CoordinateFactory.createCoordinate(effectiveValue.extractValueWithExpectation(String.class));

        CellDTO cell = sheet.activeCells().get(coordinate);

        //TODO: we need to separate the two scenarios that we got an empty cell (INBOUND) or cell out of bounds. ASK AVIAD...
        if(cell == null) {
            throw new IllegalArgumentException("Cell not found");
        }

        return cell.effectiveValue();
    }
}
