package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.expression.type.TrinaryExpression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Sub extends TrinaryExpression {
    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2, Expression expression3) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        EffectiveValue effectiveValue3 = expression3.evaluate(sheet);

        String source = effectiveValue1.extractValueWithExpectation(String.class);
        Double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);
        Double arg3 = effectiveValue3.extractValueWithExpectation(Double.class);

        if (source == null || arg2 == null || arg3 == null) {
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        if (arg2 % 1 != 0 || arg3 % 1 != 0) {
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        int startIndex = arg2.intValue();
        int endIndex = arg3.intValue();

        if(startIndex > endIndex || startIndex < 0 || endIndex > source.length()) {
            return new EffectiveValueImpl(CellType.ERROR, "!UNDEFINED!");
        }

        return new EffectiveValueImpl(CellType.TEXT, source.substring(startIndex, endIndex + 1));
    }

    public CellType getFunctionResultType() {
        return CellType.TEXT;
    }
}
