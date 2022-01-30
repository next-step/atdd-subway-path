package nextstep.subway.applicaion.dto;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public static SectionRequest of(long upStationId, long downStationId, int distance) {
        SectionRequest result = new SectionRequest();
        result.upStationId = upStationId;
        result.downStationId = downStationId;
        result.distance = distance;

        return result;
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
