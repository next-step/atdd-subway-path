package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;

    public PathService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(PathRequest request) {
        List<Line> lines = lineRepository.findAll();
        Map<Long, Station> stationById = lines.stream().flatMap(line -> line.getStations().stream())
                .collect(Collectors.toMap(Station::getId, station -> station, (a, b) -> a));

        Set<SectionEdge> edges = lines.stream().flatMap(line -> line.getSections().stream())
                .map(section -> new SectionEdge(section.getUpStation().getId(),
                        section.getDownStation().getId(), section.getDistance()))
                .collect(Collectors.toSet());

        PathFinder finder = new PathFinder(edges);
        Path path = finder.findPath(request.getSource(), request.getTarget());
        return new PathResponse(path.getShortestPath().stream().map(id -> {
            Station station = stationById.get(id);
            return new StationResponse(station.getId(), station.getName());
        }).collect(
                Collectors.toList()), path.getDistance());
    }
}
