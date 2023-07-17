package nextstep.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.request.StationRequest;
import nextstep.subway.service.response.StationResponse;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
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

    public List<StationResponse> findAllIn(List<Long> idList) {
        return stationRepository.findAllByIdIn(idList)
            .stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public Station findById(long upStationId) {
        return stationRepository.findById(upStationId)
            .orElseThrow(
                () -> new IllegalArgumentException(upStationId + " 번호에 해당하는 지하철역이 존재하지 않습니다.")
            );
    }

}
