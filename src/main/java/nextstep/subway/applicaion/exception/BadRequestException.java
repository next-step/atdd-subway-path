package nextstep.subway.applicaion.exception;


import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException {

    public final static String MESSAGE = "잘못된 요청입니다.";
    public final static String STATION_NOT_FOUND = "존재하지 않는 역입니다.";
    public final static String LINE_NOT_FOUND = "존재하지 않는 노선입니다.";

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
