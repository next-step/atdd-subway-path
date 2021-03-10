package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest(Builder builder) {
        this.upStationId = builder.upStationId;
        this.downStationId = builder.downStationId;
        this.distance = builder.distance;
    }

    public static Builder Builder() {
        return new Builder();
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

    public Section toSection(Station upStation, Station downStation) {
        return Section.Builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    public static class Builder{
        private Long upStationId;
        private Long downStationId;
        private int distance;

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

        public SectionRequest build() {
            return new SectionRequest(this);
        }

    }
}
