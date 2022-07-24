package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathResponse getPaths(Long source, Long target) {
        Station sourceStation = stationRepository.findById(source)
                .orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않아요."));

        Station targetStation = stationRepository.findById(target)
                .orElseThrow(() -> new IllegalArgumentException("도착역이 존재하지 않아요."));

        PathGraph pathGraph = new PathGraph(sectionRepository.findAll());
        StationPath shortestPath = pathGraph.getShortestPath(sourceStation, targetStation);

        return PathResponse.of(shortestPath);
    }
}
