package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.SubwayGraph;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphService {

    private LineService lineService;

    public GraphService(LineService lineService) {
        this.lineService = lineService;
    }

    public SubwayGraph findGraph() {
        return new SubwayGraph(findSections());
    }

    private List<Section> findSections() {
        return lineService.findLines().stream()
                .map(it -> it.getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
