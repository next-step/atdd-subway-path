package nextstep.subway.applicaion.dto;

public class SectionRequest {

    private Long upStationId;       // 상행 종점
    private Long downStationId;     // 하행 종점
    private int distance;           // 거리

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
