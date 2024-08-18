package engine.expression.api;

import dto.SheetDTO;
import engine.sheet.api.EffectiveValue;

public interface Expression {
    EffectiveValue evaluate(SheetDTO currentWorkingSheet);
}
