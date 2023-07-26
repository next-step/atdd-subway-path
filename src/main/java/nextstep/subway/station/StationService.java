package nextstep.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;
    private StationConverter stationConverter;

    public StationService(StationRepository stationRepository, StationConverter stationConverter) {
        this.stationRepository = stationRepository;
        this.stationConverter = stationConverter;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return stationConverter.convert(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(stationConverter::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("station is not existed by id > " + id));
    }

    public List<Station> findByIds(List<Long> stationIds) {
        return stationRepository.findByIdIn(stationIds);
    }
}
