package engine.impl.function;

import engine.Function;

public abstract class BinaryFunction<T> implements Function<T> {
    private final Function<T> function1;
    private final Function<T> function2;

    public BinaryFunction(Function<T> function1, Function<T> function2) {
        this.function1 = function1;
        this.function2 = function2;
    }

    @Override
    public T evaluate() {
        return evaluate(function1.evaluate(), function2.evaluate());
    }

    abstract protected T evaluate(T evaluate, T evaluate2);
}
