package nextstep.subway.applicaion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.SectionEdge;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            .map(section -> new SectionEdge(section.getUpStation().getId().toString(),
                section.getDownStation().getId().toString(), section.getDistance()))
            .collect(Collectors.toSet());

        PathFinder finder = new PathFinder(edges);
        Path path = finder.findPath(request.getSource(), request.getTarget());
        return new PathResponse(path.getShortestPath().stream().map(id -> {
            Station station = stationById.get(Long.parseLong(id));
            return new StationResponse(station.getId(), station.getName());
        }).collect(
            Collectors.toList()), path.getDistance());
    }
}
