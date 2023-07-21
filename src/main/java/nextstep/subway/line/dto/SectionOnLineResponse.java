package nextstep.subway.line.dto;

import nextstep.subway.section.domain.Section;

public class SectionOnLineResponse {
    private final Long id;
    private final Long upStationId;
    private final String upStationName;
    private final Long downStationId;
    private final String downStationName;
    private final Integer distance;

    public static SectionOnLineResponse of(Section section) {
        return new SectionOnLineResponse(
                section.getId(),
                section.getUpStationId(),
                section.getUpStationName(),
                section.getDownStationId(),
                section.getDownStationName(),
                section.getDistance()
        );
    }

    public SectionOnLineResponse(Long id, Long upStationId, String upStationName, Long downStationId, String downStationName, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public Integer getDistance() {
        return distance;
    }
}
