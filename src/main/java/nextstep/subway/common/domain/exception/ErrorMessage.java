package nextstep.subway.common.domain.exception;

public enum ErrorMessage {
    INVALID_SECTION_SIZE("노선에 지하철역은 최소 1개 이상 존재해야 합니다."),
    STATIONS_EXISTS("상행역 또는 하행역이 모두 노선에 이미 존재 합니다."),
    STATIONS_NOT_EXISTS("상행역 또는 하행역이 노선에 존재하지 않습니다."),
    DISTANCE_EXCEEDED("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을 수 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
