package nextstep.subway.domain;

public class BusinessException extends RuntimeException {

    public BusinessException(final String message) {
        super(message);
    }
}
