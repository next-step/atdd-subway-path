package nextstep.subway.applicaion.path;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.path.response.PathResponse;
import nextstep.subway.applicaion.station.response.StationResponse;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.global.SubwayException;
import nextstep.subway.support.SubwayShortestPath;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathResponse findShortestPath(final Long sourceId, final Long targetId) {
        if (Objects.equals(sourceId, targetId)) {
            throw new SubwayException(String.format("출발역과 도착역이 동일합니다: 출발역id=%d, 도착역id=%d", sourceId, targetId));
        }

        final var source = stationRepository.getById(sourceId);
        final var target = stationRepository.getById(targetId);

        final var shortestPath = shortestPathOf(source, target);

        return new PathResponse(
                StationResponse.toResponses(shortestPath.getStation()),
                shortestPath.getDistance()
        );
    }

    private SubwayShortestPath shortestPathOf(final Station source, final Station target) {
        final var stations = stationRepository.findAll();
        final var sections = lineRepository.findAll().stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toUnmodifiableList());

        return SubwayShortestPath.builder(stations, sections)
                .source(source)
                .target(target)
                .build();
    }
}
