package nextstep.subway.path.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.repository.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathResponse findShortestPath(long source, long target) {

        Station startStation = findStationById(source);
        Station endStation = findStationById(target);

        PathFinder.PathFinderRequest pathFinderRequest = PathFinder.PathFinderRequest.builder()
                .startStation(startStation)
                .endStation(endStation)
                .vertexList(stationRepository.findAll())
                .edgeList(lineRepository.findAll())
                .build();

        GraphPath<Station, DefaultWeightedEdge> graphPath = PathFinder.of(pathFinderRequest).searchShortestPath();
        List<Station> resultStations = graphPath.getVertexList();

        List<StationResponse> stationResponses = resultStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return PathResponse.of(stationResponses, graphPath.getWeight());
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException(id));
    }
}
