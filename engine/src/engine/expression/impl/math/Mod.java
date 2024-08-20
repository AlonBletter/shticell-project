package engine.expression.impl.math;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Mod extends BinaryExpression {

    public Mod(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        Double arg1 = effectiveValue1.extractValueWithExpectation(Double.class);
        Double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);

        if (arg1 == null || arg2 == null) {
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() + " function!\n" +
                    "Expected Arg1=<"+ CellType.NUMERIC +">, Arg2=<" + CellType.NUMERIC + "> but received " +
                    "Arg1=<" + effectiveValue1.getCellType() + ">, Arg2=<" + effectiveValue2.getCellType() + ">");
        }

        return new EffectiveValueImpl(CellType.NUMERIC, arg1 % arg2);
    }
}
