package engine.expression.impl.math;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Abs extends UnaryExpression {
    public Abs(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);
        double result = effectiveValue.extractValueWithExpectation(Double.class);
        //TODO: Optional<T> OR exception OR null
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }
}
