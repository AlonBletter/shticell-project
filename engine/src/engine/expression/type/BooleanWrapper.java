package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class BooleanWrapper implements Expression {
    private final Boolean aBoolean;

    public BooleanWrapper(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    @Override
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(CellType.BOOLEAN, aBoolean);
    }

    @Override
    public String toString() {
        return aBoolean ? "true" : "false";
    }
}
