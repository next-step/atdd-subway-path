package nextstep.subway.exception;

/**
 * 비즈니스 로직 내 논리적 예외를 던집니다.
 */
public class SubwayException extends RuntimeException {

    public SubwayException(String message) {
        super(message);
    }
}
