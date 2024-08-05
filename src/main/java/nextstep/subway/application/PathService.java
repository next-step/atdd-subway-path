package nextstep.subway.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.*;
import nextstep.subway.infrastructure.SectionRepository;
import nextstep.subway.infrastructure.StationRepository;
import nextstep.subway.presentation.PathResponse;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathResponse getPath(Long sourceId, Long targetId) {
        List<Section> allSections = sectionRepository.findAll();
        List<Station> allStations = stationRepository.findAll();

        Station sourceStation = stationRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("출발역을 찾을 수 없습니다."));
        Station targetStation = stationRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("도착역을 찾을 수 없습니다."));

        PathFinder pathFinder = PathFinderFactory.createPathFinder(allSections, allStations);
        PathResult pathResult = pathFinder.getShortestPath(sourceStation, targetStation);

        return PathResponse.of(pathResult.getPathStations(), pathResult.getTotalDistance());
    }
}
