package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MapService {

    private final LineService lineService;

    public MapService(LineService lineService) {
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public MapResponse getMaps() {
        final List<LineResponse> lines = lineService.findAllLines().stream()
                .map(LineResponse::getId)
                .map(lineService::findLineById)
                .collect(Collectors.toList());
        return MapResponse.of(lines);
    }

}
