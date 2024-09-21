package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Equal extends BinaryExpression {

    public Equal(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);

        CellType type1 = effectiveValue1.getCellType();
        CellType type2 = effectiveValue2.getCellType();

        if(type1 == type2) {
            return new EffectiveValueImpl(CellType.BOOLEAN, effectiveValue1.getValue().equals(effectiveValue2.getValue()));
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, false);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}