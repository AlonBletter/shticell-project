package engine.expression.api;

import engine.expression.impl.logic.*;
import engine.expression.impl.math.*;
import engine.expression.impl.string.Concat;
import engine.expression.impl.string.Sub;
import engine.expression.impl.system.Ref;
import engine.sheet.cell.api.CellType;

import javax.naming.NoPermissionException;
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
    TIMES {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Times.class, parameters);
            return new Times(parameters.get(0), parameters.get(1));
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
    },
    SUM {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Sum.class, parameters);
            return new Sum(parameters.getFirst());
        }
    },
    AVERAGE {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Average.class, parameters);
            return new Average(parameters.getFirst());
        }
    },
    PERCENT {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Percent.class, parameters);
            return new Percent(parameters.get(0), parameters.get(1));
        }
    },
    EQUAL {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Equal.class, parameters);
            return new Equal(parameters.get(0), parameters.get(1));
        }
    },
    NOT {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Not.class, parameters);
            return new Not(parameters.getFirst());
        }
    },
    BIGGER {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Bigger.class, parameters);
            return new Bigger(parameters.get(0), parameters.get(1));
        }
    },
    LESS {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Less.class, parameters);
            return new Less(parameters.get(0), parameters.get(1));
        }
    },
    OR {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(Or.class, parameters);
            return new Or(parameters.get(0), parameters.get(1));
        }
    },
    AND {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(And.class, parameters);
            return new And(parameters.get(0), parameters.get(1));
        }
    },
    IF {
        @Override
        public Expression createExpression(List<Expression> parameters) {
            validateNumberOfParameters(If.class, parameters);
            return new If(parameters.get(0), parameters.get(1), parameters.get(2));
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