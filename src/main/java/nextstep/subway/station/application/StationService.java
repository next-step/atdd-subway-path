package nextstep.subway.station.application;

import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllById(List<Long> ids) {
        return stationRepository.findAllById(ids).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
