package nextstep.subway.applicaion.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private int statusCode;
    private String statusMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.statusMessage = message;
    }
}
