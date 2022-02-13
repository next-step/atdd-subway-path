package nextstep.subway.applicaion;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphService {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    public PathResponse getShortestPath(Long source, Long target) {
        List<Line> allLines = lineRepository.findAll();
        Graph graph = new Graph(allLines);

        List<Long> path = graph.dijkstraShortestPath(source, target);
        int shortestDistance = graph.dijkstraShortestDistance(path);

        List<StationResponse> stationResponses = path.stream().map(stationId ->
                        new StationResponse(stationRepository.findById(stationId).get()))
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, shortestDistance);
    }
}
