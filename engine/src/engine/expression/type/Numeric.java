package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Numeric implements Expression {
    private final double number;

    public Numeric(double number) {
        this.number = number;
    }

    @Override
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(CellType.NUMERIC, number);
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }
}
