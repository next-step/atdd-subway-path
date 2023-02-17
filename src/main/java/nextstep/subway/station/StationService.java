package nextstep.subway.station;

import static nextstep.subway.common.SubwayErrorMsg.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return createStationResponse(station);
    }

	@Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

	public List<Station> findAllStation() {
		return stationRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Station findStationById(Long id) {
		return stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_STATION.isMessage()));
	}

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station);
    }
}
