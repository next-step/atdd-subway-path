package nextstep.subway.line.ui.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LineErrorCode implements ErrorCode {
    ILLEGAL_SECTION_OPERATION(HttpStatus.BAD_REQUEST, "Illegal Section Operation");

    private final HttpStatus httpStatus;
    private final String message;
}
