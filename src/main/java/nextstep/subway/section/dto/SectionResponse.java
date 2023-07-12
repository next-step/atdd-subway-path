package nextstep.subway.section.dto;


import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public SectionResponse(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
