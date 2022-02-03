package nextstep.subway.applicaion.exception;

public class NotExistStationException extends RuntimeException {
    final static String message = "해당 역은 등록되지 않았습니다. - [%d]";

    public NotExistStationException(Long id) {
        super(String.format(message, id));
    }
}
