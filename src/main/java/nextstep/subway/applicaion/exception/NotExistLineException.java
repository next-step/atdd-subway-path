package nextstep.subway.applicaion.exception;

public class NotExistLineException extends RuntimeException {
    final static String message = "해당 노선은 등록되지 않았습니다. - [%d]";

    public NotExistLineException(Long id) {
        super(String.format(message, id));
    }
}