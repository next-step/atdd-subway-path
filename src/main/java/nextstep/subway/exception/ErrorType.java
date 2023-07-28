package nextstep.subway.exception;

public enum ErrorType {
    STATIONS_NOT_EXIST_IN_LINE("생성하려는 구간의 모든 역이 노선에 존재하지 않습니다."),
    STATIONS_EXIST_IN_LINE("생성하려는 구간의 모든 역이 노선에 이미 존재합니다.");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return null;
    }
}
