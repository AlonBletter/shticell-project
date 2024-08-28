package engine.expression.impl.math;

import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Abs extends UnaryExpression {
    public Abs(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);

        Double result = effectiveValue.extractValueWithExpectation(Double.class);

        if (result == null) {
            return new EffectiveValueImpl(CellType.ERROR, Double.NaN);
        }

        return new EffectiveValueImpl(CellType.NUMERIC, Math.abs(result));
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
