package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long startStationId, Long arrivalStationId) {
        Station startStation = stationService.findById(startStationId);
        Station arrivalStation = stationService.findById(arrivalStationId);
        List<Line> lines = lineService.findAllLines();

        List<Section> sections = getAllSections(lines);
        Path graphPath = new Path(sections, startStation, arrivalStation);

        List<StationResponse> stationResponses = createStationResponses(graphPath);
        int weight = graphPath.getWeight();

        return new PathResponse(stationResponses, weight);
    }

    private List<StationResponse> createStationResponses(Path graphPath) {
        List<Station> shortestPath = graphPath.getVertexes();
        return shortestPath.stream()
                .map(this::toStationResponse)
                .collect(Collectors.toList());
    }

    private List<Section> getAllSections(List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());
    }

    public StationResponse toStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }
}
