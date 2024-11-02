package dto.requestinfo;

import dto.coordinate.Coordinate;

public record UpdateInformation(Coordinate coordinate, String value, int version) {
}
