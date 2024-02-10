package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations(List<Station> stations) {
        validateSize(stations);
        this.stations = new ArrayList<>(stations);
    }

    private void validateSize(List<Station> stations) {
        if (stations.isEmpty()) {
            throw new ApplicationException("지하철역이 존재하지 않습니다.");
        }
    }

    public Station findStationBy(Long stationId) {
        return stations.stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("존재하지 않는 역입니다."));
    }
}
