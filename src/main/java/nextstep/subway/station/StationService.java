package nextstep.subway.station;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.domain.PathEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public StationResponse saveStation(StationRequest stationRequest, PathEvent event) {
        eventPublisher.publishEvent(event);

        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id, PathEvent event) {
        eventPublisher.publishEvent(event);

        stationRepository.deleteById(id);
    }

    public StationResponse findStation(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(
                () -> new EntityNotFoundException("지하철 역을 찾을 수 없습니다.")
        );

        return createStationResponse(station);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
