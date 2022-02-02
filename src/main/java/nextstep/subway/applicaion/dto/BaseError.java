package nextstep.subway.applicaion.dto;

public class BaseError {
    private String message;


    public BaseError() {
    }

    public BaseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
