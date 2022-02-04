package nextstep.subway.applicaion.exception;

public class NotExistException extends RuntimeException {
    final static String message = "해당 정보는 등록되지 않았습니다. - [%s]";

    public NotExistException(Long id) {
        super(String.format(message, id.toString()));
    }

    public NotExistException(String name) {
        super(String.format(message, name));
    }
}