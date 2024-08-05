package nextstep.subway.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SubwayExceptionType {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error"),
    STATION_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "Entity Not Found. stationId: %s"),
    LINE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "Entity Not Found. lineId: %s"),

    INVALID_UP_STATION(HttpStatus.BAD_REQUEST.value(), "Invalid Up Station. upStationId: %s"),
    INVALID_DOWN_STATION(HttpStatus.BAD_REQUEST.value(), "Invalid Down Station. downStationId: %s"),
    SECTION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(),
        "Section is already existed. upStationId: %s, downStationId: %s"),
    INVALID_SECTION_LENGTH(HttpStatus.BAD_REQUEST.value(), "Invalid Section Length. distance: %s"),

    CANNOT_DELETE_SINGLE_SECTION(HttpStatus.BAD_REQUEST.value(), "Cannot Delete Single Section"),
    CANNOT_DELETE_NON_LAST_DOWN_STATION(HttpStatus.BAD_REQUEST.value(),
        "Cannot Delete Non-Last Down Station"),

    SOURCE_AND_TARGET_SAME(HttpStatus.BAD_REQUEST.value(),
        "Source and Target cannot be the same"),

    PATH_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "Path not found between the given stations");


    private final int code;
    private final String message;
}
