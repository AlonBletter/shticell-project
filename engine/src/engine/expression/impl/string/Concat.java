package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Concat extends BinaryExpression {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        String arg1 = effectiveValue1.extractValueWithExpectation(String.class);
        String arg2 = effectiveValue2.extractValueWithExpectation(String.class);

        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.TEXT, "!UNDEFINED!");
        }

        return new EffectiveValueImpl(CellType.TEXT, arg1 + arg2);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.TEXT;
    }
}
