package engine.expression.impl.string;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.TrinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Sub extends TrinaryExpression {
    public Sub(Expression expression1, Expression expression2, Expression expression3) {
        super(expression1, expression2, expression3);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2, Expression expression3) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        EffectiveValue effectiveValue3 = expression3.evaluate(sheet);

        String source = effectiveValue1.extractValueWithExpectation(String.class);
        Double arg2 = effectiveValue2.extractValueWithExpectation(Double.class);
        Double arg3 = effectiveValue3.extractValueWithExpectation(Double.class);

        if (source == null || arg2 == null || arg3 == null) {
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() + " function!\n" +
                    "Expected Arg1=<"+ CellType.NUMERIC +">, Arg2=<" + CellType.TEXT + ">, Arg3=<" + CellType.TEXT + "> but received " +
                    "Arg1=<" + effectiveValue1.getCellType() + ">, Arg2=<" + effectiveValue2.getCellType() + ">, Arg3=<" + effectiveValue2.getCellType() + ">");
        }

        if (arg2 % 1 != 0 || arg3 % 1 != 0) {
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() +
                    " function! Indices must be whole numbers. Received Arg2=<" + arg2 + ">, Arg3=<" + arg3 + ">");
        }

        int startIndex = arg2.intValue();
        int endIndex = arg3.intValue();

        if(startIndex > endIndex || startIndex < 0 || endIndex > source.length()) {
            return new EffectiveValueImpl(CellType.TEXT, "!UNDEFINED!");
        }

        return new EffectiveValueImpl(CellType.TEXT, source.substring(startIndex, endIndex + 1));
    }
}
