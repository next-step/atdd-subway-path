package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.DuplicateException;
import nextstep.subway.exception.NotFoundStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(final StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        if (isDuplicate(stationName)) {
            throw new DuplicateException();
        }
        Station station = stationRepository.save(new Station(stationName));
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station findStationById(final Long id) {
        return stationRepository.findById(id)
                .orElseThrow(NotFoundStationException::new);
    }

    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }

    private boolean isDuplicate(final String name) {
        return findByName(name).isPresent();
    }

    private Optional<Station> findByName(String name) {
        return stationRepository.findByName(name);
    }
}
