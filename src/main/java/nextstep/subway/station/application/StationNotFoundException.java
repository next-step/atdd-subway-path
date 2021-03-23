package nextstep.subway.station.application;

import nextstep.subway.common.NotFoundException;

public class StationNotFoundException extends NotFoundException {
    public StationNotFoundException() {
        super("지하철역을 찾을 수 없습니다.");
    }
}
