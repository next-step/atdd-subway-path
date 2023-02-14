package nextstep.subway.domain.exception;

public class NotFoundLineException extends RuntimeException {

    public NotFoundLineException() {
        super("해당하는 노선을 찾을 수 없습니다.");
    }

}
