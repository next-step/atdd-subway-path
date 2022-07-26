package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        Station upStation = findByStationId(source);
        Station downStation = findByStationId(target);
        List<Line> lines = findLines();

        JGraph subwayGraph = new JGraph(lines);
        PathFinder path = new PathFinder(subwayGraph);

        return PathResponse.of(path, upStation, downStation);
    }

    private List<Line> findLines() {
        return lineRepository.findAll();
    }

    private Station findByStationId(Long id) {
        return stationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
