package engine.expression.api;

import engine.sheet.api.SheetReadActions;
import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;

public interface Expression {
    EffectiveValue evaluate(SheetReadActions currentWorkingSheet);
    CellType getFunctionResultType();
}
