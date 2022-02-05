package nextstep.subway.path.application;

import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.path.application.dto.PathRequest;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.StationPaths;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PathFacade {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathResponse findShortestPaths(PathRequest pathRequest) {
        Station source = stationService.findById(pathRequest.getSource());
        Station target = stationService.findById(pathRequest.getTarget());
        StationPaths stationPaths = allSections().shortestPaths(pathFinder, source, target);

        return PathResponse.from(stationPaths);
    }

    private Sections allSections() {
        return lineRepository.findAllWithStations()
                             .stream()
                             .map(Line::getSections)
                             .reduce(Sections::union)
                             .orElse(new Sections(Collections.emptyList()));
    }
}
