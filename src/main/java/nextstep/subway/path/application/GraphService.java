package nextstep.subway.path.application;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GraphService {

    public GraphService() {

    }

    public SubwayGraph findGraph(List<Section> sections) {
        return new SubwayGraph(getStations(sections), sections);
    }

    private static List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .map(it -> it.getStations())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
