package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author a1101466 on 2022/07/13
 * @project subway
 * @description
 */
@Getter
@Setter
public class ErrorResponse {

    private String message;
    private int httpStatus;


    public ErrorResponse(String message, int httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
