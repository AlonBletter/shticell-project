package engine.expression.api;

import engine.sheet.api.EffectiveValue;

public interface Expression {
    EffectiveValue evaluate();
}
