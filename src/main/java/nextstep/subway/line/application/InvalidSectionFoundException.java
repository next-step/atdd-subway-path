package nextstep.subway.line.application;

public class InvalidSectionFoundException extends RuntimeException {
    public InvalidSectionFoundException(String message) {
        super(message);
    }
}
