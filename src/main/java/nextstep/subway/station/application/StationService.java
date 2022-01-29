package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.domain.model.exception.EntityNotFoundException;
import nextstep.subway.common.domain.model.exception.FieldDuplicateException;
import nextstep.subway.station.application.dto.StationRequest;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.repository.StationRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class StationService {
    private static final String ENTITY_NAME_FOR_EXCEPTION = "지하철 역";

    private final StationRepository stationRepository;

    public Station findById(Long id) {
        return stationRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME_FOR_EXCEPTION));
    }

    public StationResponse saveStation(StationRequest request) {
        if (stationRepository.existsByName(request.getName())) {
            throw new FieldDuplicateException("역 이름");
        }
        Station station = stationRepository.save(
            new Station(request.getName())
        );

        return StationResponse.from(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }
}
