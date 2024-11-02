package engine.expression.type;

import engine.expression.api.Expression;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import dto.effectivevalue.EffectiveValueImpl;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanWrapper that = (BooleanWrapper) o;
        return Objects.equals(aBoolean, that.aBoolean);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(aBoolean);
    }
}
