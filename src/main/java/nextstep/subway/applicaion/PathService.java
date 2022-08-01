package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.domain.vo.Path;
import nextstep.subway.exception.StationNotExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        SubwayMap subwayMap = new SubwayMap(lines);
        Path path = subwayMap.shortestPath(stationFrom(source), stationFrom(target));
        return new PathResponse(toStationResponses(path.getStations()), path.getDistance());
    }

    private Station stationFrom(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new StationNotExistException("id에 해당하는 역이 존재하지 않습니다."));
    }

    private List<StationResponse> toStationResponses(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList());
    }
}
