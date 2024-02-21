package nextstep.subway.line.exception;

public class SectionException extends RuntimeException {

    private String message = "잘못된 구간 정보입니다.";

    public SectionException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
