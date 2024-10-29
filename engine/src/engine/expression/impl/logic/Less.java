package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.SheetReadActions;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import dto.effectivevalue.EffectiveValueImpl;

public class Less extends BinaryExpression {

    public Less(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);

        CellType type1 = effectiveValue1.cellType();
        CellType type2 = effectiveValue2.cellType();

        if(type1 != CellType.NUMERIC || type2 != CellType.NUMERIC) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        Double arg1 = effectiveValue1.extractValueWithExpectation(Double.class);
        Double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);

        boolean result = arg1 <= arg2;

        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}