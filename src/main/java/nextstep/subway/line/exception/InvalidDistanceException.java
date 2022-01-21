package nextstep.subway.line.exception;

public class InvalidDistanceException extends LineDomainException {
    public static final String MESSAGE = "기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.";

    public InvalidDistanceException() {
        super(MESSAGE);
    }
}
