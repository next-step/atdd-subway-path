package nextstep.subway.applicaion.line;

import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    protected final LineRepository lineRepository;
    protected final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }
}
