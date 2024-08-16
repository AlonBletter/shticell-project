package engine.expression.impl.math;

import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.impl.EffectiveValueImpl;

public class Divide extends BinaryExpression {

    public Divide(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate();
        EffectiveValue effectiveValue2 = expression2.evaluate();

        double arg1 = effectiveValue1.extractValueWithExpectation(Double.class);
        double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);

        if(arg2 == 0) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }

        return new EffectiveValueImpl(CellType.NUMERIC, arg1 / arg2);
    }
}
