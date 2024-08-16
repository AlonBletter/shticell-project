package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Text implements Expression {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(CellType.TEXT, text);
    }

    @Override
    public String toString() {
        return text;
    }
}
