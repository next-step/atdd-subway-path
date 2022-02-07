package nextstep.subway.exception;

public class IllegalDistanceException extends RuntimeException {
    private static final String MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음";

    public IllegalDistanceException() {
        super(MESSAGE);
    }
}
