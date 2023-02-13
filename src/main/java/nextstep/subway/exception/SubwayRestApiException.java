package nextstep.subway.exception;

public class SubwayRestApiException extends RuntimeException {
    private final ErrorResponseEnum errorResponseEnum;

    public SubwayRestApiException(ErrorResponseEnum errorResponseEnum) {
        this.errorResponseEnum = errorResponseEnum;
    }

    public ErrorResponseEnum getErrorResponse() {
        return errorResponseEnum;
    }
}
