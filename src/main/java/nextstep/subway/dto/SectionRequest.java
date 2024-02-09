package nextstep.subway.dto;

public class SectionRequest {

    private Long upStationId;

    private Long downStationId;

    private int distance;

    private Long stationLineId;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance, Long stationLineId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.stationLineId = stationLineId;
    }

    public static SectionRequest mergeForCreateLine(Long stationLineId, SectionRequest request) {
        return new SectionRequest(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance(),
                stationLineId
        );
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

    public Long getLineId() {
        return stationLineId;
    }
}
