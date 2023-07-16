package subway.exception;

import subway.line.constant.SubwayMessage;

public class SubwayException extends RuntimeException {

    private final ErrorResponse response;

    public SubwayException(SubwayMessage subwayMessage) {
        this.response = ErrorResponse.builder()
                .code(subwayMessage.getCode())
                .message(subwayMessage.getMessage())
                .build();
    }

    public SubwayException(final long code, final String message) {
        this.response = ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public String getMessage() {
        return this.response.getMessage();
    }

    public ErrorResponse getResponse() {
        return response;
    }


}
