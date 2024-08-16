package engine.impl.function;

import engine.Function;

public class Number implements Function<Double> {
    private final double number;

    public Number(double number) {
        this.number = number;
    }

    @Override
    public Double evaluate() {
        return number;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }
}
