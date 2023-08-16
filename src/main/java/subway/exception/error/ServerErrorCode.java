package subway.exception.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ServerErrorCode {

    BAD_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 요청입니다.")
    ;

    private HttpStatus statusCode;
    private String message;
}
