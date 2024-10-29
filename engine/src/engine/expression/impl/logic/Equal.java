package engine.expression.impl.logic;

import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.SheetReadActions;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import dto.effectivevalue.EffectiveValueImpl;

public class Equal extends BinaryExpression {

    public Equal(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);

        CellType type1 = effectiveValue1.cellType();
        CellType type2 = effectiveValue2.cellType();

        if(type1 == type2) {
            return new EffectiveValueImpl(CellType.BOOLEAN, effectiveValue1.value().equals(effectiveValue2.value()));
        }

        return new EffectiveValueImpl(CellType.BOOLEAN, false);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}