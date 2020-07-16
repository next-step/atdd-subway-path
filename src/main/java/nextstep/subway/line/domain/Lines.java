package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.dto.LineResponses;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Long> getAllStationIdsOfLines() {
        return lines.stream()
            .flatMap(Line::getStationIds)
            .distinct()
            .collect(Collectors.toList());
    }

    public LineResponses toLineResponses(AllStations stations) {
        return lines.stream()
            .map(line -> line.toLineResponse(stations))
            .collect(collectingAndThen(Collectors.toList(), LineResponses::new));
    }
}
