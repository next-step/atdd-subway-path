package nextstep.subway.exception;

public abstract class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

}
