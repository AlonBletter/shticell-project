package engine;

public interface Cell {
    String getOriginalValue();
    Function<?> getEffectiveValue();
}
