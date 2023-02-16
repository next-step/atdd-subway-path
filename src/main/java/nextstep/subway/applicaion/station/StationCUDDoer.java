package nextstep.subway.applicaion.station;

import nextstep.subway.applicaion.dto.station.StationRequest;
import nextstep.subway.applicaion.dto.station.StationResponse;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StationCUDDoer extends StationService{
    public StationCUDDoer(StationRepository stationRepository) {
        super(stationRepository);
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

}
