package nextstep.subway.controller.request;

public class SectionAddRequest {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionAddRequest() {
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
