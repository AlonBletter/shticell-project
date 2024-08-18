package engine.expression.type;

import dto.SheetDTO;
import engine.sheet.api.EffectiveValue;
import engine.expression.api.Expression;

public abstract class BinaryExpression implements Expression {
    private final Expression expression1;
    private final Expression expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue evaluate(SheetDTO currentWorkingSheet) {
        return evaluate(currentWorkingSheet, expression1, expression2);
    }

    abstract protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2);
}
