package engine.expression.impl.string;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Concat extends BinaryExpression {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        String result = effectiveValue1.extractValueWithExpectation(String.class) + effectiveValue2.extractValueWithExpectation(String.class);

        return new EffectiveValueImpl(CellType.TEXT, result);
    }
}
