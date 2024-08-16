package engine.expression.impl.math;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Minus extends BinaryExpression {

    public Minus(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate();
        EffectiveValue effectiveValue2 = expression2.evaluate();
        double result = effectiveValue1.extractValueWithExpectation(Double.class) - effectiveValue2.extractValueWithExpectation(Double.class);

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }
}
