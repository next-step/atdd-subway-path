package nextstep.subway.line.ui.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LineErrorCode implements ErrorCode {

    CANT_DELETE_SECTION(HttpStatus.BAD_REQUEST, "Can't Delete Section"),
    CANT_ADD_SECTION(HttpStatus.BAD_REQUEST, "Can't Add Section"),
    CANT_SUBTRACT_SECTION(HttpStatus.BAD_REQUEST, "Can't Subtract Section"),
    CANT_COMBINE_SECTION(HttpStatus.BAD_REQUEST, "Can't Combine Section")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
