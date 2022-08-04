package nextstep.subway.domain;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Lines {

    private List<Line> lines;

    public List<Station> getStations() {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Station getStation(Long stationId) {
        return getStations().stream()
                .filter(station -> station.isEqualTo(stationId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

}
