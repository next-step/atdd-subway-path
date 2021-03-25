package nextstep.subway.path;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PathService {

    private final StationRepository stationRepository;
    private final LineService lineService;

    public PathService(StationRepository stationRepository, LineService lineService) {
        this.stationRepository = stationRepository;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {

        Station source = findStationById(sourceId);
        Station target = findStationById(targetId);
        List<Section> sections = lineService.getSections();

        return new PathResponse(Collections.emptyList(), 0);
    }

    private Station findStationById(Long sourceId) {
        return stationRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("역이 존재하지 않습니다"));
    }
}
