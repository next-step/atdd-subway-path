package nextstep.subway.exception;

public enum ErrorMessage {
    MISMATCHED_UPSTREAM_STATION_EXCEPTION("구간의 상행역과 해당 노선에 등록된 하행 종점역과 일치하지 않아 등록할 수 없습니다!"),
    NON_DOWNSTREAM_TERMINUS_EXCEPTION("하행 종점역이 아니기 때문에 제거할 수 없습니다!"),
    SINGLE_SEGMENT_REMOVAL_EXCEPTION("구간이 하나뿐이기 때문에 구간을 제거할 수 없습니다!"),
    LINE_NOT_FOUND_EXCEPTION("요청한 노선이 존재하지 않습니다!"),
    STATION_NOT_FOUND_EXCEPTION("요청한 스테이션이 존재하지 않습니다!"),
    DOWNSTREAM_STATION_INCLUDED_EXCEPTION("구간 하행역이 이미 해당 노선에 포함되어 있어서 등록할 수 없습니다!"),
    INVALID_DISTANCE_EXCEPTION("역 사이의 거리가 유효하지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
