package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

public class LineStationsStep {

    public static List<Long> 지하철역을_순서대로_반환한다(LineStations lineStations) {
        return lineStations.getStationsInOrder().stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
