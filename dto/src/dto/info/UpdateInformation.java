package dto.info;

import engine.sheet.coordinate.Coordinate;

public record UpdateInformation(Coordinate coordinate, String value, int version) {
}
