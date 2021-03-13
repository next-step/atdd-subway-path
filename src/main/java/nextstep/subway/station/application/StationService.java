package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.NoResourceException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationService {

  private StationRepository stationRepository;

  public StationService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
  }

  public StationResponse saveStation(StationRequest stationRequest) {
    Station persistStation = stationRepository.save(stationRequest.toStation());
    return StationResponse.of(persistStation);
  }

  @Transactional(readOnly = true)
  public List<StationResponse> findAllStations() {
    List<Station> stations = stationRepository.findAll();

    return stations.stream()
        .map(station -> StationResponse.of(station))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Station findStation(long stationId) {
    return stationRepository.findById(stationId)
        .orElseThrow(() -> new NoResourceException("등록되지 않은 역 입니다."));
  }

  public void deleteStationById(Long id) {
    stationRepository.deleteById(id);
  }
}
