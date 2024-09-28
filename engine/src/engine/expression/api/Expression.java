package engine.expression.api;

import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.SheetReadActions;

public interface Expression {
    EffectiveValue evaluate(SheetReadActions currentWorkingSheet);
    CellType getFunctionResultType();
}
