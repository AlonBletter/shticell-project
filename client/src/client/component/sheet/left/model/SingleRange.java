package client.component.sheet.left.model;

import dto.coordinate.Coordinate;

import java.util.List;

public class SingleRange {
    private String name;
    private Coordinate start;
    private Coordinate end;
    private List<Coordinate> cellsInRange;

    public SingleRange(String name, Coordinate start, Coordinate end, List<Coordinate> cellsInRange) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.cellsInRange = cellsInRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getStart() {
        return start;
    }

    public void setStart(Coordinate start) {
        this.start = start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public void setEnd(Coordinate end) {
        this.end = end;
    }

    public List<Coordinate> getCellsInRange() {
        return cellsInRange;
    }

    public void setCellsInRange(List<Coordinate> cellsInRange) {
        this.cellsInRange = cellsInRange;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
