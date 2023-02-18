package nextstep.subway.applicaion.station;

import nextstep.subway.applicaion.dto.station.StationResponse;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationFinder extends StationService{

    public StationFinder(StationRepository stationRepository) {
        super(stationRepository);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(a -> StationResponse.of(a))
                .collect(Collectors.toList());
    }
}
