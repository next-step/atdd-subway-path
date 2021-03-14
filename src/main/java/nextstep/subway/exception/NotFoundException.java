package nextstep.subway.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message + "을 찾을 수 없습니다.");
    }
}
