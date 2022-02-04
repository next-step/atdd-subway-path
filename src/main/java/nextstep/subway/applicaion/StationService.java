package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    /* 역 생성을 처리한다. */
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = createStation(stationRequest.getName());
        return StationResponse.from(station);
    }

    private Station createStation(String name) {
        return stationRepository.save(Station.from(name));
    }

    /* 모든 역의 정보를 반환한다. */
    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    /* 역 삭제를 처리한다. */
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
