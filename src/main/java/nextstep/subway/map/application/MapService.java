package nextstep.subway.map.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class MapService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public MapService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public MapResponse getMaps() {
        return null;
    }
}
