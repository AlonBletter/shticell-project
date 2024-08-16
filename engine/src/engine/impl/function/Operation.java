package engine.impl.function;

import engine.Function;
import engine.impl.function.math.binary.*;
import engine.impl.function.math.unary.*;
import java.lang.reflect.Constructor;
import java.util.List;

@SuppressWarnings("unchecked")
public enum Operation {
    DIVIDE {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Divide.class, parameters);

            return new Divide((Function<Number>) parameters.get(0), (Function<Number>) parameters.get(1));
        }
    },
    MINUS {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Minus.class, parameters);

            return new Minus((Function<Number>) parameters.get(0), (Function<Number>) parameters.get(1));
        }
    },
    MOD {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Mod.class, parameters);

            return new Mod((Function<Number>) parameters.get(0), (Function<Number>) parameters.get(1));
        }
    },
    PLUS {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Plus.class, parameters);

            return new Plus((Function<Number>) parameters.get(0), (Function<Number>) parameters.get(1));
        }
    },
    POW {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Pow.class, parameters);

            return new Pow((Function<Number>) parameters.get(0), (Function<Number>) parameters.get(1));
        }
    },
    ABS {
        @Override
        public Function<?> createFunction(List<Function<?>> parameters) {
            validateParameters(Abs.class, parameters);

            return new Abs((Function<Number>) parameters.getFirst());
        }
    };


    public abstract Function<?> createFunction(List<Function<?>> parameters);
    
    private static void validateParameters(Class<?> operationClazz, List<Function<?>> parameters) {
        try {
            Constructor<?> constructor = operationClazz.getConstructor();
            int numberOfCtorParameters = constructor.getParameterCount();
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            if (numberOfCtorParameters != parameters.size()) {
                throw new IllegalArgumentException("Expected " + numberOfCtorParameters + " parameters, but got " + parameters.size());
            }

            // PROBABLY BUGGED ---------------------------------------------------
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(parameters.get(i).getClass())) {
                    throw new IllegalArgumentException("Parameter " + i + " must be of type " + parameterTypes[i].toString());
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid arguments was given to the constructor", e);
        }
    }
}
