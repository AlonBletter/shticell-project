package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.UnaryExpression;
import engine.sheet.api.SheetReadActions;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import dto.effectivevalue.EffectiveValueImpl;

public class Not extends UnaryExpression {
    public Not(Expression expression) {
        super(expression);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression) {
        EffectiveValue effectiveValue = expression.evaluate(sheet);

        Boolean result = effectiveValue.extractValueWithExpectation(Boolean.class);

        if (result == null) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        result = !result;

        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}