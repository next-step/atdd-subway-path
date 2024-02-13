package nextstep.subway.exception;

public class SingleSectionDeleteException extends RuntimeException{
    public SingleSectionDeleteException(String message) {
        super(message);
    }

    public SingleSectionDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
