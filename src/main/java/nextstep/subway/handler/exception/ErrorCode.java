package nextstep.subway.handler.exception;

public enum ErrorCode {
    STATION_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 지하철역을 찾을 수 없습니다."),
    LINE_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 노선을 찾을 수 없습니다."),
    ;

    private final int status;
    private final String detail;

    ErrorCode(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
