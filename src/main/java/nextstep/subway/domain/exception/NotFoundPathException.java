package nextstep.subway.domain.exception;

public class NotFoundPathException extends RuntimeException {

    public NotFoundPathException() {
        super("경로를 찾을 수 없습니다");
    }

}
