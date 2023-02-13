package nextstep.subway.domain;

public enum PathErrorMessage {
    FIND_PATH_SAME_TARGET_AND_SOURCE("출발역과 도착역이 같은 경로를 조회할 수 없습니다."),
    FIND_PATH_NOT_CONNECTED("출발역과 도착역이 연결되어 있지 않은 경로를 조회할 수 없습니다."),
    FIND_PATH_STATION_NOT_EXIST("존재하지 않는 역이 포함된 경로를 조회할 수 없습니다.");

    private final String message;

    PathErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
