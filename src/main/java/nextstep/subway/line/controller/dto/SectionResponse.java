package nextstep.subway.line.controller.dto;

import nextstep.subway.line.domain.Section;

/** 구간 응답 DTO */
public class SectionResponse {

    private long id;
    private long upStationId;
    private long downStationId;
    private int distance;

    private SectionResponse(long id, long upStationId, long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse ofEntity(Section section) {
        return new SectionResponse(
            section.getId(),
            section.getUpStation().getId(),
            section.getDownStation().getId(),
            section.getDistance()
        );
    }

    public long getId() {
        return id;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
