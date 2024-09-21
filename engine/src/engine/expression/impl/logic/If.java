package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.TrinaryExpression;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class If extends TrinaryExpression {
    public If(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2, Expression expression3) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        EffectiveValue effectiveValue3 = expression3.evaluate(sheet);

        Boolean source = effectiveValue1.extractValueWithExpectation(Boolean.class);

        if (source == null) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        if(source) {
            return effectiveValue2;
        } else {
            return effectiveValue3;
        }
    }

    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
