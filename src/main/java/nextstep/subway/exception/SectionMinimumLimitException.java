package nextstep.subway.exception;

public class SectionMinimumLimitException extends RuntimeException {

    public SectionMinimumLimitException() {
        super("최소 유지 구간보다 작아질 수 없습니다.");
    }

}
