package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Or extends BinaryExpression {

    public Or(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);

        CellType type1 = effectiveValue1.getCellType();
        CellType type2 = effectiveValue2.getCellType();

        if(type1 != CellType.BOOLEAN || type2 != CellType.BOOLEAN) {
            return new EffectiveValueImpl(CellType.ERROR, "UNKNOWN");
        }

        Boolean arg1 = effectiveValue1.extractValueWithExpectation(Boolean.class);
        Boolean arg2 = effectiveValue2.extractValueWithExpectation(Boolean.class);

        return new EffectiveValueImpl(CellType.BOOLEAN, arg1 || arg2);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
