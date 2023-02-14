package nextstep.subway.domain.exception;

public class DuplicateAddSectionException extends RuntimeException {

    public DuplicateAddSectionException() {
        super("이미 추가되어있는 구간 요청입니다.");
    }
}
