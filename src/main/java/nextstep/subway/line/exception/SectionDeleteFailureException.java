package nextstep.subway.line.exception;

public class SectionDeleteFailureException extends RuntimeException {

    private String message = "구간 삭제에 실패했습니다.";

    public SectionDeleteFailureException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
