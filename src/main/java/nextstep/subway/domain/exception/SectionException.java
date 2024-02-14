package nextstep.subway.domain.exception;

public class SectionException extends DomainException {

    public static final String INVALID_DISTANCE = "구간의 거리는 0보다 커야 합니다.";

    public SectionException(String message) {
        super(message);
    }

    public static class InvalidSectionException extends SectionException {

        public InvalidSectionException() {
            super("구간을 추가할 수 없습니다.");
        }
    }
}
