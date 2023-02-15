package nextstep.subway.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    STATION_NOT_FOUND("해당하는 지하철 역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LINE_NOT_FOUND("해당하는 지하철 노선을 찾을 수 없습니다.", HttpStatus.NOT_FOUND)
    ;

    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(final String message, final HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
