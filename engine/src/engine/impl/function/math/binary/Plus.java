package engine.impl.function.math.binary;

import engine.Function;
import engine.impl.function.Number;
import engine.impl.function.BinaryFunction;

public class Plus extends BinaryFunction<Number> {

    public Plus(Function<Number> function1, Function<Number> function2) {
        super(function1, function2);
    }

    @Override
    protected Number evaluate(Number e1, Number e2) {
        return new Number(e1.evaluate() + e2.evaluate());
    }
}
