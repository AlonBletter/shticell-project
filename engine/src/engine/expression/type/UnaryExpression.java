package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.api.EffectiveValue;

public abstract class UnaryExpression implements Expression {
    private final Expression expression;

    public UnaryExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue evaluate() {
        return evaluate(expression);
    }

    abstract protected EffectiveValue evaluate(Expression expression);
}
