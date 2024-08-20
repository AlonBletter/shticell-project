package engine.expression.api;

import engine.exception.ParameterCountMismatchException;
import engine.expression.impl.math.*;
import engine.expression.impl.string.Concat;
import engine.expression.impl.string.Sub;
import engine.expression.impl.system.Ref;

import java.lang.reflect.Constructor;
import java.util.List;

public enum Operation {
    DIVIDE {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Divide.class, parameters);
            return new Divide(parameters.get(0), parameters.get(1));
        }
    },
    MINUS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Minus.class, parameters);
            return new Minus(parameters.get(0), parameters.get(1));
        }
    },
    MOD {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Mod.class, parameters);
            return new Mod(parameters.get(0), parameters.get(1));
        }
    },
    PLUS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Plus.class, parameters);
            return new Plus(parameters.get(0), parameters.get(1));
        }
    },
    POW {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Pow.class, parameters);
            return new Pow(parameters.get(0), parameters.get(1));
        }
    },
    ABS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Abs.class, parameters);
            return new Abs(parameters.getFirst());
        }
    },
    CONCAT {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Concat.class, parameters);
            return new Concat(parameters.get(0), parameters.get(1));
        }
    },
    SUB {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Sub.class, parameters);
            return new Sub(parameters.get(0), parameters.get(1), parameters.get(2));
        }
    },
    REF {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Ref.class, parameters);
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
            //throw new ParameterCountMismatchException(constructorParameterCount, parameters.size());
            throw new IllegalArgumentException("Expected " + parameters.size() + " parameters but received " + constructorParameterCount);
        }
    }
}