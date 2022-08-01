package nextstep.subway.exception;

public class NotRegisteredInAllSectionsException extends BadRequestException {
    public NotRegisteredInAllSectionsException(String message) {
        super(message);
    }
}
