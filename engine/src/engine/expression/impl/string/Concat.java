package engine.expression.impl.string;

import dto.SheetDTO;
import engine.expression.api.Expression;
import engine.expression.type.BinaryExpression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class Concat extends BinaryExpression {

    public Concat(Expression expression1, Expression expression2) {
        super(expression1, expression2);
    }

    @Override
    protected EffectiveValue evaluate(SheetDTO sheet, Expression expression1, Expression expression2) {
        EffectiveValue effectiveValue1 = expression1.evaluate(sheet);
        EffectiveValue effectiveValue2 = expression2.evaluate(sheet);
        String arg1 = effectiveValue1.extractValueWithExpectation(String.class);
        String arg2 = effectiveValue2.extractValueWithExpectation(String.class);

        if (arg1 == null || arg2 == null) {
            //TODO consider adding "getExpressionResultType" method to expression interface and then check: DYNAMIC VALIDATION
            // CellType leftCellType = left.getFunctionResultType();
            // CellType rightCellType = right.getFunctionResultType();
            // if ( (!leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN)) ||
            //      (!rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN)) ) {
            //      return new EffectiveValueImpl(CellType.TEXT, "!UNDEFINED!");
            // }

            //TODO this could be moved to Operation?? static validation
            throw new IllegalArgumentException("Invalid arguments to " + this.getClass().getSimpleName().toUpperCase() + " function!\n" +
                    "Expected Arg1=<"+ CellType.TEXT +">, Arg2=<" + CellType.TEXT + "> but received " +
                    "Arg1=<" + effectiveValue1.getCellType() + ">, Arg2=<" + effectiveValue2.getCellType() + ">");
        }

        return new EffectiveValueImpl(CellType.TEXT, arg1 + arg2);
    }
}
