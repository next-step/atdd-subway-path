package nextstep.subway.applicaion;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

public class PathService {

    private final StationRepository stationRepository;

    public PathService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public void findShortestPath(final Long sourceId, final Long targetId) {
        final Station sourceStation = stationRepository.findById(sourceId).orElseThrow((IllegalArgumentException::new));
        final Station targetStation = stationRepository.findById(targetId).orElseThrow((IllegalArgumentException::new));
        if (sourceStation.isSameName(targetStation)) {
            throw new IllegalArgumentException("source and target stations were conflict");
        }
    }
}
