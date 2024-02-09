package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SectionResponse {

    private Long id;

    @JsonIgnore
    private Long upStationId;

    @JsonIgnore
    private Long downStationId;

    @JsonIgnore
    private int distance;

    public SectionResponse(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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
}
