package nextstep.subway.service;

import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.StationRequest;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        return station.createStationResponse();
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(Station::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }



    public Station findById(Long id) {
        return stationRepository.findById(id).get();
    }


}
