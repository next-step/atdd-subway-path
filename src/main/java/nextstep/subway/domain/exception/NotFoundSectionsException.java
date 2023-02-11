package nextstep.subway.domain.exception;

public class NotFoundSectionsException extends RuntimeException {

    public NotFoundSectionsException() {
        super("해당하는 구간을 찾지 못했습니다.");
    }
}
