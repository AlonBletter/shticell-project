package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

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
}
