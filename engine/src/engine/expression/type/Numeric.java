package engine.expression.type;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;
import engine.sheet.effectivevalue.EffectiveValueImpl;

public class Numeric implements Expression {
    private final double number;

    public Numeric(double number) {
        this.number = number;
    }

    @Override
    public EffectiveValue evaluate(SheetReadActions currentWorkingSheet) {
        return new EffectiveValueImpl(CellType.NUMERIC, number);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }
}
