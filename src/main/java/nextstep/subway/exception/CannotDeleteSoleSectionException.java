package nextstep.subway.exception;

public class CannotDeleteSoleSectionException extends RuntimeException {

    public static final String MESSAGE = "지하철 노선에 등록된 구간이 하나이므로 제거할 수 없습니다.";

    public CannotDeleteSoleSectionException() {
        super(MESSAGE);
    }
}
