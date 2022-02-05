package nextstep.subway.applicaion;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(final StationRepository stationRepository, final LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public void findShortestPath(final Long sourceId, final Long targetId) {
        final Station sourceStation = stationRepository.findById(sourceId).orElseThrow((IllegalArgumentException::new));
        final Station targetStation = stationRepository.findById(targetId).orElseThrow((IllegalArgumentException::new));
        if (sourceStation.isSameName(targetStation)) {
            throw new IllegalArgumentException("source and target stations were conflict");
        }
    }
}
