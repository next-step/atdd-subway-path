package nextstep.subway.exception;

public class SectionNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Section 이 존재하지 않습니다.";

    public SectionNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public SectionNotFoundException(String message) {
        super(message);
    }
}
