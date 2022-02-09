package nextstep.subway.applicaion.exception;

public class NotExistSectionException extends RuntimeException {
    final static String message = "해당 역이 등록된 구간이 없습니다. - [%s]";

    public NotExistSectionException(Long id) {
        super(String.format(message, id));
    }

    public NotExistSectionException(String name) {
        super(String.format(message, name));
    }
}
