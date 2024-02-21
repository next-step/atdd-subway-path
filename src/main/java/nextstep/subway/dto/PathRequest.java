package nextstep.subway.dto;

public class PathRequest {

    Long departureStationId;

    Long arrivalStationId;

    public PathRequest(Long departureStationId, Long arrivalStationId) {
        this.departureStationId = departureStationId;
        this.arrivalStationId = arrivalStationId;
    }

    public Long getDepartureStationId() {
        return departureStationId;
    }

    public Long getArrivalStationId() {
        return arrivalStationId;
    }
}
