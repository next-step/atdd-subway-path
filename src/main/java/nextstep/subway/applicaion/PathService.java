package nextstep.subway.applicaion;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;

public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }
}
