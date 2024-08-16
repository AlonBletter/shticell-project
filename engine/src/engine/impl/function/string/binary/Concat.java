package engine.impl.function.string.binary;

import engine.Function;
import engine.impl.function.BinaryFunction;

public class Concat extends BinaryFunction<String> {

    public Concat(Function<String> function1, Function<String> function2) {
        super(function1, function2);
    }

    @Override
    protected String evaluate(String evaluate, String evaluate2) {
        return evaluate + evaluate2;
    }
}
