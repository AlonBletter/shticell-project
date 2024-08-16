package engine.impl.function;

import engine.Function;

public abstract class UnaryFunction<T> implements Function<T> {
    private final Function<T> function;

    public UnaryFunction(Function<T> function) {
        this.function = function;
    }

    @Override
    public T evaluate() {
        return evaluate(function.evaluate());
    }

    abstract protected T evaluate(T evaluate);
}
