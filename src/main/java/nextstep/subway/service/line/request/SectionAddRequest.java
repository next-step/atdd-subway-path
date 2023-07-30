package nextstep.subway.service.line.request;

public class SectionAddRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionAddRequest(Long upStationId, Long downStationId, Integer distance) {
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
