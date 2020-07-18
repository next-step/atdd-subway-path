package nextstep.subway.path.domain;

import nextstep.subway.line.domain.LineStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LineStations {
    private final List<LineStation> lineStations;
    private final Integer distance;
    private final Integer duration;

    private LineStations(List<LineStation> lineStations) {
        this.lineStations = Collections.unmodifiableList(new ArrayList<>(lineStations));
        this.distance = this.lineStations.stream().mapToInt(LineStation::getDistance).sum();
        this.duration = this.lineStations.stream().mapToInt(LineStation::getDuration).sum();
    }

    public static LineStations from(List<Long> stationIds, List<LineStation> allLineStations) {
        List<LineStation> foundLineStations = new ArrayList<>();
        for (int i = 1; i < stationIds.size(); i++) {
            Long stationId = stationIds.get(i);
            Long preStationId = stationIds.get(i - 1);

            LineStation lineStation = allLineStations.stream()
                    .filter(station -> Objects.equals(station.getStationId(), stationId))
                    .filter(station -> Objects.equals(preStationId, station.getPreStationId()))
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            foundLineStations.add(lineStation);
        }

        return new LineStations(foundLineStations);
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
