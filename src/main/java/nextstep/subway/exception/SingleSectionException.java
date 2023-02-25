package nextstep.subway.exception;

public class SingleSectionException extends RuntimeException {

    private static final String SINGLE_SECTION_ERROR = "지하철 노선에 구간이 1개인 경우에는 역을 삭제할 수 없습니다.";

    public SingleSectionException() {
        super(SINGLE_SECTION_ERROR);
    }
}
