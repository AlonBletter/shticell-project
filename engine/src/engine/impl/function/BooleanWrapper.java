package engine.impl.function;

import engine.Function;

public class BooleanWrapper implements Function<Boolean> {
    private final Boolean aBoolean;

    public BooleanWrapper(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    @Override
    public Boolean evaluate() {
        return aBoolean;
    }

    @Override
    public String toString() {
        return aBoolean ? "true" : "false";
    }
}
