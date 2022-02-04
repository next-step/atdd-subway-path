package nextstep.subway.applicaion.exception;

public class DuplicateException extends RuntimeException {
    final static String message = "중복된 정보로 생성할 수 없습니다. - [%s]";

    public DuplicateException(String name) {
        super(String.format(message, name));
    }
}
