package engine.expression.type;

import engine.sheet.effectivevalue.EffectiveValue;
import engine.expression.api.Expression;
import engine.sheet.api.SheetReadActions;

public abstract class BinaryExpression implements Expression {
    private final Expression expression1;
    private final Expression expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return evaluate(currentWorkingSheet, expression1, expression2);
    }

    abstract protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression1, Expression expression2);
}
