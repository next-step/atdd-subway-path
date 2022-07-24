package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class CantDeleteLastSectionException extends BusinessException {

    private static final String CANT_DELETE_LAST_ONE_SECTION_EXCEPTION = "노선의 마지막 하나 남은 구간은 삭제할 수 없습니다";

    public CantDeleteLastSectionException() {
        super(CANT_DELETE_LAST_ONE_SECTION_EXCEPTION);
    }
}
