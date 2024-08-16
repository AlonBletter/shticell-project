package engine.impl.function.system;

import engine.Function;
import engine.impl.function.Text;
import engine.impl.function.UnaryFunction;

public class Ref extends UnaryFunction<Text> {
    public Ref(Function<Text> function) {
        super(function);
    }

    @Override
    protected Text evaluate(Text e) {
        return new Text(""); // WRONG
    }
}
