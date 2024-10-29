package engine.expression.type;

import engine.expression.api.Expression;
import dto.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;

public abstract class TrinaryExpression implements Expression {
    private Expression expression1;
    private Expression expression2;
    private Expression expression3;

    public TrinaryExpression(Expression expression1, Expression expression2, Expression expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return evaluate(currentWorkingSheet, expression1, expression2, expression3);
    }

    abstract protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2, Expression expression3);
}
