package nextstep.subway.domain;

import nextstep.subway.exception.StationNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class Lines {
    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Station> getAllStations() {
        return lines.stream()
            .flatMap(it -> it.getAllStations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Section> getAllSections() {
        return lines.stream()
            .flatMap(it -> it.getSections().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Station> getStationsByIds(List<Long> ids) {
        return ids
            .stream()
            .map(this::findById)
            .collect(Collectors.toList());
    }

    private Station findById(long id) {
        return getAllStations().stream()
            .filter(station -> station.getId().equals(id))
            .findFirst()
            .orElseThrow(StationNotFoundException::new);
    }
}
