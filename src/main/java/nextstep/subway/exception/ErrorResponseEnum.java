package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorResponseEnum {
    ERROR_NO_FOUND (HttpStatus.INTERNAL_SERVER_ERROR,"do not found data by id"),
    ERROR_INVAILD_DISTANCE (HttpStatus.INTERNAL_SERVER_ERROR,"section's distance is not valid"),
    ERROR_ADD_SECTION_INVAILD_STATION (HttpStatus.INTERNAL_SERVER_ERROR,"section's stations is already all exist or no exist"),
    ERROR_DELETE_SECTION_INVAILD_STATION (HttpStatus.INTERNAL_SERVER_ERROR,"stations is no exist in line"),
    ERROR_DELETE_SECTION_COUNT_LINE (HttpStatus.INTERNAL_SERVER_ERROR, "line have only one section"),
    ERROR_FOUND_PATH_SAME_SOURCE_TARGET (HttpStatus.INTERNAL_SERVER_ERROR, "source station is same target station"),
    ERROR_FOUND_PATH_NO_EXIST_STATION (HttpStatus.INTERNAL_SERVER_ERROR, "source or target station is not exist"),
    ERROR_FOUND_PATH_NO_CONNECTION_STATION (HttpStatus.INTERNAL_SERVER_ERROR, "source station is not connected target station");

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
