package nextstep.subway.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;

    public ErrorResponse(SubwayException se) {
        this.errorCode = se.getErrorCode();
    }
}
