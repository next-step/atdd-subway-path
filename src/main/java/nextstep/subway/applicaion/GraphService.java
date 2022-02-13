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
        Station sourceStation = stationRepository.findById(source).get();
        Station targetStation = stationRepository.findById(target).get();

        Graph graph = new Graph(allLines);

        List<Station> path = graph.dijkstraShortestPath(sourceStation, targetStation);

        int shortestDistance = graph.dijkstraShortestDistance(path);

        List<StationResponse> stationResponses = path.stream().map(station ->
                        new StationResponse(station))
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, shortestDistance);
    }
}
