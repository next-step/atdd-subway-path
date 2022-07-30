package nextstep.subway.enums.exceptions;

public enum ErrorCode {
    NOT_FOUND_STATION(404, "STATION-ERR", "해당 역을 찾을 수 없습니다."),
    NOT_FOUND_SECTION(404, "SECTION-ERR", "저장된 구간이 없습니다."),
    NOT_FOUND_LINE(404, "LINE-ERR", "해당 노선은 없습니다."),
    NOT_ENOUGH_SECTION(500, "SECTION-ERR", "구간이 1개 밖에 없습니다."),
    IS_NOT_SAME_LAST_STATION(400, "SECTION-ERR", "삭제하고자 하는 역은 해당 노선에 마지막 역이어야 합니다.");
    private final int status;
    private final String errorCode;
    private final String message;

    ErrorCode(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
