package nextstep.subway.station.ui.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StationErrorCode implements ErrorCode {

    CANT_DELETE_STATION(HttpStatus.BAD_REQUEST, "Can't Delete Station"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
