package engine.impl.function.math.unary;

import engine.Function;
import engine.impl.function.Number;
import engine.impl.function.UnaryFunction;

public class Abs extends UnaryFunction<Number> {
    public Abs(Function<Number> function) {
        super(function);
    }

    @Override
    protected Number evaluate(Number e) {
        return new Number(
                Math.abs(e.evaluate()));
    }
}
