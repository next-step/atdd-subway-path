package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PathService {

    //기존에는 LineRepository를 참조했지만, LineService를 참고하도록 바꿨다.
    //서비스가 다른 서비스의 레파지토리에 대한 정보까지 알고 있으면, 해당 서비스 + 해당 서비스 안의 레파지토리 까지 모두 autowired하여 의존이 중복되고, 불필요한 autowired까지 필요해질 수 있기 때문이다.
    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse getPath(Long sourceId, Long targetId) {
        if(Objects.equals(sourceId, targetId)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }

        List<Line> lines = lineService.findAll();
        Station start = stationService.findById(sourceId);
        Station end = stationService.findById(targetId);
        List<Section> sections = lines.stream().map(Line::getSections).flatMap(Collection::stream).collect(Collectors.toList());

        Set<Station> stations = lineService.getAllStations(lines);
        Path path = new Path(stations, sections);
        return new PathResponse(StationResponse.createStationResponses(path.getShortestPath(start, end))
                , path.getShortestWeight(start, end));
    }
}
