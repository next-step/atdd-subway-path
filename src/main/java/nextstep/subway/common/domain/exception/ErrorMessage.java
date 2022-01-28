package nextstep.subway.common.domain.exception;

public enum ErrorMessage {
    NOT_LAST_STATION_DELETED("노선에 포함된 역중 마지막 역만 삭제할 수 있습니다."),
    EXISTS_STATIONS("상행역 또는 하행역이 모두 노선에 이미 존재 합니다."),
    NOT_EXISTS_STATIONS("상행역 또는 하행역이 노선에 존재하지 않습니다."),
    DISTANCE_EXCEEDED("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을 수 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
