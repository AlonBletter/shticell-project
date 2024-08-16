package engine.impl.function;

import engine.Function;

public abstract class TrinaryFunction<T> implements Function<T> {
    private Function<T> function1;
    private Function<T> function2;
    private Function<T> function3;

    @Override
    public T evaluate() {
        return evaluate(function1.evaluate(), function2.evaluate(), function3.evaluate());
    }

    abstract protected T evaluate(T evaluate, T evaluate1, T evaluate2);
}
