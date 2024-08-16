package engine.expression.impl.system;

import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

public class Ref extends UnaryExpression {
    public Ref(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate();

        //Coordinate cellId = CoordinateFactory.createCoordinate(effectiveValue.extractValueWithExpectation(String.class);

        return null;
    }
}
