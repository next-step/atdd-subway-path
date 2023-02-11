package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorResponseEnum {
    ERROR_NO_FOUND (HttpStatus.INTERNAL_SERVER_ERROR,"do not found data by id"),
    ERROR_ADD_SECTION_INVAILD_DISTANCE (HttpStatus.INTERNAL_SERVER_ERROR,"section's distance is not enough"),
    ERROR_ADD_SECTION_INVAILD_STATION (HttpStatus.INTERNAL_SERVER_ERROR,"section's stations is already all exist or no exist"),
    ERROR_DELETE_SECTION_INVAILD_STATION (HttpStatus.INTERNAL_SERVER_ERROR,"stations is no exist in line");

    HttpStatus httpStatus;
    String message;

    ErrorResponseEnum(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.message;
    }
}
