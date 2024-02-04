package nextstep.subway.line.controller.dto;

/** 구간 등록 요청 DTO */
public class SectionAddRequest {

    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionAddRequest() {}

    public SectionAddRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
