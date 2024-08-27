package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class BooleanWrapper implements Expression {
    private final Boolean aBoolean;

    public BooleanWrapper(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return new EffectiveValueImpl(CellType.BOOLEAN, aBoolean);
    }

    @Override
    public String toString() {
        return aBoolean ? "TRUE" : "FALSE";
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
