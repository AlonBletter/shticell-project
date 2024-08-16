package engine.exception;

public class ParameterCountMismatchException extends IllegalArgumentException {
    private final int expectedCount;
    private final int actualCount;

    public ParameterCountMismatchException(int expectedCount, int actualCount) {
        super("Expected " + expectedCount + " parameters, but got " + actualCount);
        this.expectedCount = expectedCount;
        this.actualCount = actualCount;
    }

    public int getExpectedCount() {
        return expectedCount;
    }

    public int getActualCount() {
        return actualCount;
    }
}
