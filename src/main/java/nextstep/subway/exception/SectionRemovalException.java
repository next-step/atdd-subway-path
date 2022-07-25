package nextstep.subway.exception;

public class SectionRemovalException extends RuntimeException {

    private static final String SECTION_REMOVAL_EXCEPTION = "구간이 1개인 경우 (상행 종점, 하행 종점 만 존재하는 경우)에는 " +
            "역을 삭제할 수 없습니다";

    public SectionRemovalException() {
        super(SECTION_REMOVAL_EXCEPTION);
    }

}