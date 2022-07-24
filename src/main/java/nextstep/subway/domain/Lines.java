package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Lines {

    private List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

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
                .distinct()
                .collect(Collectors.toList());
    }
}
