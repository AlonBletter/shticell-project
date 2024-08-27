package engine.expression.type;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.SheetReadActions;

public abstract class UnaryExpression implements Expression {
    private final Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return evaluate(currentWorkingSheet, expression);
    }

    abstract protected EffectiveValue evaluate(SheetReadActions sheet, Expression expression);
}
