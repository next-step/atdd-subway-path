package nextstep.subway.exception;

public class SectionDistanceNotValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

    public SectionDistanceNotValidException() {
        super(message);
    }
}
