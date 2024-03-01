package nextstep.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.repository.StationRepository;

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
        return StationResponse.createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                                .map(StationResponse::createStationResponse)
                                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public StationResponse findStationById(Long id) {
        return stationRepository.findById(id)
                                .map(station -> new StationResponse(station.getId(), station.getName()))
                                .orElseThrow(() -> new NoStationException(id + "에 해당하는 지하철 역이 없습니다."));
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                                .orElseThrow(() -> new NoStationException(id + "에 해당하는 지하철 역이 없습니다."));
    }

    public StationResponse findStationByName(String name) {
        return stationRepository.findStationByName(name)
                                .map(station -> new StationResponse(station.getId(), station.getName()))
                                .orElseThrow(() -> new NoStationException(name + "에 해당하는 지하철 역이 없습니다."));
    }
}
