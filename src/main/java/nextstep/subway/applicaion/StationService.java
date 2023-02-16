package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public StationService(final StationRepository stationRepository, final StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역 입니다."));
    }

    public List<StationResponse> findAllStations() {
        return stationMapper.toResponseFrom(stationRepository.findAll());
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return stationMapper.toResponseFrom(station);
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
