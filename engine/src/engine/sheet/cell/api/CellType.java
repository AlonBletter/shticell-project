package engine.sheet.cell.api;

public enum CellType {
    NUMERIC(Double.class),
    TEXT(String.class),
    BOOLEAN(Boolean.class),
    UNKNOWN(void.class),
    EMPTY(void.class),
    ERROR(void.class);

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }

    public static CellType getCellType(Class<?> aType) {
        for (CellType cellType : CellType.values()) {
            if (cellType.isAssignableFrom(aType)) {
                return cellType;
            }
        }

        return null;
    }
}

