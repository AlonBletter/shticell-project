package engine.impl.function;

import engine.Function;

public class Text implements Function<String> {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public String evaluate() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
