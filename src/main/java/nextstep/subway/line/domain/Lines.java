package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class Lines {

    private List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Section> findAllSections() {
        return lines.stream()
                    .flatMap(line -> line.getSections().stream())
                    .collect(Collectors.toList());
    }

    public List<Station> findAllStations() {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .collect(Collectors.toList());
    }
}
