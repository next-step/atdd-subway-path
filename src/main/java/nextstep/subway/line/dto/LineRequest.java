package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineRequest(Builder builder) {
        name = builder.name;
        color = builder.color;
        upStationId = builder.upStationId;
        downStationId = builder.downStationId;
        distance = builder.distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public Line toLine(Section section) {
        return new Line(name, color, section);
    }

    public Section toSection(Station upStation, Station downStation) {
        return Section.Builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    public static class Builder{
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public Builder() {
        }

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder color(String val) {
            this.color = val;
            return this;
        }

        public Builder upStationId(Long val) {
            this.upStationId = val;
            return this;
        }

        public Builder downStationId(Long val) {
            this.downStationId = val;
            return this;
        }

        public Builder distance(int val) {
            this.distance = val;
            return this;
        }

        public LineRequest build() {
            return new LineRequest(this);
        }

    }
}
