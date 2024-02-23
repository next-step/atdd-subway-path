package nextstep.subway.web.service;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.domians.repository.StationRepository;
import nextstep.subway.web.dto.request.StationCreateRequest;
import nextstep.subway.web.dto.response.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationCreateRequest stationCreateRequest) {
        Station station = stationRepository.save(new Station(stationCreateRequest.getName()));
        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
            .map(this::createStationResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
            station.getId(),
            station.getName()
        );
    }
}
