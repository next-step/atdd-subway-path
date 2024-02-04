package nextstep.subway.line.exception;

/** 지하철 노선에 구간 등록 실패 시 던짐 */
public class SectionAddFailureException extends RuntimeException {

    private String message = "구간 등록에 실패했습니다.";

    public SectionAddFailureException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
