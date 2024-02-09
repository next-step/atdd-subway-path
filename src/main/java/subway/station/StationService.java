package subway.station;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.station.StationRequest;
import subway.dto.station.StationResponse;

@Service
@Transactional(readOnly = true)
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public Station findStationById(Long stationId) {
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 정류장입니다."));
	}

	@Transactional
	public StationResponse saveStation(StationRequest stationRequest) {
		Station station = stationRepository.save(new Station(stationRequest.getName()));
		return StationResponse.of(station);
	}

	public List<StationResponse> findAllStations() {
		return stationRepository.findAll().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}
}
