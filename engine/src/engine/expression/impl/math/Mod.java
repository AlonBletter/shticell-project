package engine.expression.impl.math;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.SheetReadActions;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import dto.effectivevalue.EffectiveValueImpl;

public class Mod extends BinaryExpression {

    public Mod(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        Double arg1 = effectiveValue1.extractValueWithExpectation(Double.class);
        Double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);

        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.ERROR, Double.NaN);
        }

        return new EffectiveValueImpl(CellType.NUMERIC, arg1 % arg2);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
