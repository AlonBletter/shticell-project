package engine.expression.impl.string;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.TrinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Sub extends TrinaryExpression {
    private Expression expression1;
    private Expression expression2;
    private Expression expression3;

    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2, Expression expression3) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        EffectiveValue effectiveValue3 = expression3.evaluate(sheet);

        String source = effectiveValue1.extractValueWithExpectation(String.class);
        int startIndex = effectiveValue2.extractValueWithExpectation(Double.class).intValue();
        int endIndex = effectiveValue3.extractValueWithExpectation(Double.class).intValue();

        if(startIndex > endIndex || startIndex < 0 || endIndex > source.length()) {
            return new EffectiveValueImpl(CellType.TEXT, "!UNDEFINED!");
        } //TODO: how to return undefined?

        return new EffectiveValueImpl(CellType.TEXT, source.substring(startIndex, endIndex + 1));
    }
}
