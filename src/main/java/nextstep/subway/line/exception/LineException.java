package nextstep.subway.line.exception;

public class LineException extends RuntimeException {

    private String message = "잘못된 노선 정보입니다.";

    public LineException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
