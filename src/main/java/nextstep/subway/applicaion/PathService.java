package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathResponse findShortestPath(Long source, Long target) {
        var sourceStation = stationRepository.findById(source)
                .orElseThrow(IllegalArgumentException::new);
        var targetStation = stationRepository.findById(target)
                .orElseThrow(IllegalArgumentException::new);

        var path = new PathFinder(getAllSections()).solve(sourceStation, targetStation);

        return new PathResponse(
                path.getStations().stream().map(StationResponse::of).collect(Collectors.toList()),
                path.getDistance()
        );
    }

    private List<Section> getAllSections() {
        return lineRepository.findAll().stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
