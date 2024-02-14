package subway.line;

import subway.station.Station;

public class LineRequest {
    private Long id;
    private String name;
    private String color;
    private Long distance;
    private Long upStationId;
    private Long downStationId;

    public LineRequest(
            Long id, String name, String color, Long distance,
            Long upStationId, Long downStationId
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line toEntity(Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
    }
}
