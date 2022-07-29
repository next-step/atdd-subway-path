package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Lines {

    private List<Line> values;

    public Lines(List<Line> lines) {
        this.values = lines;
    }

    public boolean hasLessThanStations(int stationCount) {
        return getStations().size() < stationCount;
    }

    public List<Station> getStations() {
        return values.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Section> getSections() {
        return values.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }
}
