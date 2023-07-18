package nextstep.subway.common.exception;

// 원래는 BaseResponseEntity와 같이 만들어서 성공 / 실패와 같은 걸 표기하려 했으나, 단순화하기 위해 사용
public class BaseExceptionResponse {
    private String message;

    public BaseExceptionResponse() {
    }

    public BaseExceptionResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
