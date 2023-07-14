package nextstep.subway.common.exception;

public class ErrorResponseDto {

    private final Integer code;
    private final String message;

    public ErrorResponseDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
