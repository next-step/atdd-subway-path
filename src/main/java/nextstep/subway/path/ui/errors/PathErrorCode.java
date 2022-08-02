package nextstep.subway.path.ui.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.common.errors.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PathErrorCode implements ErrorCode {

    CANT_FIND_PATH(HttpStatus.BAD_REQUEST, "Can't Find Path"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}