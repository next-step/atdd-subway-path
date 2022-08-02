package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.PathException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathResponse getPath(PathRequest pathRequest) {
        Station startStation = getStation(pathRequest.getSource());
        Station finishStation = getStation(pathRequest.getTarget());

        PathFinder pathFinder = new PathFinder(getAllSections());
        List<Station> stationList = pathFinder.getShortesPath(startStation, finishStation);
        long distance = pathFinder.getSumOfDistance(startStation, finishStation);

        return new PathResponse(distance, createStationResponse(stationList));
    }

    private List<Section> getAllSections() {
        return lineRepository.findAll()
            .stream()
            .flatMap(line -> line.getSectionList().stream())
            .collect(Collectors.toList());
    }

    private Station getStation(long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new PathException("지하철역이 존재하지 않습니다."));
    }

    private List<StationResponse> createStationResponse(List<Station> stationList) {
        return stationList.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }
}
