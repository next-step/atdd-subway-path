package nextstep.subway.applicaion.dto;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public static SectionRequest of(long upStationId, long downStationId, int distance) {
        SectionRequest request = new SectionRequest();
        request.upStationId = upStationId;
        request.downStationId = downStationId;
        request.distance = distance;

        return request;
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
