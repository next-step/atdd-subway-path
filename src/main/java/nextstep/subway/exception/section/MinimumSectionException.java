package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class MinimumSectionException extends ServiceException {

    private static final String MESSAGE = "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없습니다.";

    public MinimumSectionException() {
        super(MESSAGE);
    }

}
