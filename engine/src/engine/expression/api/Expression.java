package engine.expression.api;

import engine.sheet.api.SheetReadActions;
import dto.cell.CellType;
import dto.effectivevalue.EffectiveValue;

public interface Expression {
    EffectiveValue evaluate(SheetReadActions currentWorkingSheet);
    CellType getFunctionResultType();
}
