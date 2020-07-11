package nextstep.subway.line.dto;

public class LineStationRequest {
    private Long stationId;
    private Long preStationId;
    private Integer distance;
    private Integer duration;

    public LineStationRequest() {
    }

    public LineStationRequest(Long stationId, Long preStationId, Integer distance, Integer duration) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}