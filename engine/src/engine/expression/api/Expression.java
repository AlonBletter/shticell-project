package engine.expression.api;

import dto.SheetDTO;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.SheetReadActions;

public interface Expression {
    EffectiveValue evaluate(SheetReadActions currentWorkingSheet);
    CellType getFunctionResultType();
}
