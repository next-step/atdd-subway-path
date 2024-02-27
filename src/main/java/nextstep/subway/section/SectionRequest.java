package nextstep.subway.section;

public class SectionRequest {

    private Long downStationId;
    private Long upStationId;
    private int distance;

    public SectionRequest() {}

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest(Section section) {
        this.upStationId = section.getUpStation().getId();
        this.downStationId = section.getDownStation().getId();
        this.distance = section.getDistance();
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

}
