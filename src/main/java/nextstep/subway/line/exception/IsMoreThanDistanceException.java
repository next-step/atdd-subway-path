package nextstep.subway.line.exception;

public class IsMoreThanDistanceException extends RuntimeException {

    public static final String MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다";

    public IsMoreThanDistanceException() {
        super(MESSAGE);
    }
}
