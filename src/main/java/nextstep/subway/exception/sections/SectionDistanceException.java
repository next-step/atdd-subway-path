package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class SectionDistanceException extends BusinessException {

    private static final String SECTION_DISTANCE_EXCEPTION = "기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다";

    public SectionDistanceException() {
        super(SECTION_DISTANCE_EXCEPTION);
    }
}
