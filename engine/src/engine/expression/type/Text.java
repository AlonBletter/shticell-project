package engine.expression.type;

import engine.expression.api.Expression;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import dto.effectivevalue.EffectiveValueImpl;

import java.util.Objects;

public class Text implements Expression {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return new EffectiveValueImpl(CellType.TEXT, text);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.TEXT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text1 = (Text) o;
        return Objects.equals(text, text1.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }
}
