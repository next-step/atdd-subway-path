package nextstep.subway.exception;

public class SectionBadRequestException extends RuntimeException {

    public SectionBadRequestException() {

    }

    public SectionBadRequestException(String message) {
        super(message);
    }
}
