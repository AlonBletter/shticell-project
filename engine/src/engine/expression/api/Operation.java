package engine.expression.api;

import engine.expression.impl.math.*;
import engine.expression.impl.string.Concat;
import engine.expression.impl.string.Sub;
import engine.expression.impl.system.Ref;
import engine.sheet.cell.api.CellType;

import java.lang.reflect.Constructor;
import java.util.List;

public enum Operation {
    DIVIDE {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Divide.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Divide(parameters.get(0), parameters.get(1));
        }
    },
    MINUS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Minus.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Minus(parameters.get(0), parameters.get(1));
        }
    },
    MOD {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Mod.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Mod(parameters.get(0), parameters.get(1));
        }
    },
    PLUS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Plus.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Plus(parameters.get(0), parameters.get(1));
        }
    },
    POW {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Pow.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Pow(parameters.get(0), parameters.get(1));
        }
    },
    ABS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Abs.class, parameters);

            CellType expressionCellType = parameters.getFirst().getFunctionResultType();

            return new Abs(parameters.getFirst());
        }
    },
    TIMES {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Times.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Times(parameters.get(0), parameters.get(1));
        }
    },
    CONCAT {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Concat.class, parameters);

            CellType leftCellType = parameters.get(0).getFunctionResultType();
            CellType rightCellType = parameters.get(1).getFunctionResultType();

            return new Concat(parameters.get(0), parameters.get(1));
        }
    },
    SUB {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Sub.class, parameters);

            CellType expressionType1 = parameters.get(0).getFunctionResultType();
            CellType expressionType2 = parameters.get(1).getFunctionResultType();
            CellType expressionType3 = parameters.get(2).getFunctionResultType();

            return new Sub(parameters.get(0), parameters.get(1), parameters.get(2));
        }
    },
    REF {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Ref.class, parameters);

            CellType expressionCellType = parameters.getFirst().getFunctionResultType();

            return new Ref(parameters.getFirst());
        }
    };

    public abstract Expression createExpression(List<Expression> parameters);

    private static void validateNumberOfParameters(Class<?> operationClazz, List<Expression> parameters) {
        boolean matchFound = false;
        int constructorParameterCount = 0;
        Constructor<?>[] constructors = operationClazz.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            constructorParameterCount = constructor.getParameterCount();
            if (constructorParameterCount == parameters.size()) {
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            throw new IllegalArgumentException("Invalid number of arguments in " + operationClazz.getSimpleName().toUpperCase() + " function!\n" +
                    "Expected [" + constructorParameterCount + "] arguments but received [" + parameters.size() + "]");
        }
    }
}