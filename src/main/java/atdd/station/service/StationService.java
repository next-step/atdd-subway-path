package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.PathResponseDto;
import atdd.station.dto.StationResponseDto;
import atdd.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationAssembler stationAssembler;
    private final StationRepository stationRepository;
    private final ShortestPathFinder shortestPathFinder;

    public StationService(StationAssembler stationAssembler,
                          StationRepository stationRepository,
                          ShortestPathFinder shortestPathFinder) {

        this.stationAssembler = stationAssembler;
        this.stationRepository = stationRepository;
        this.shortestPathFinder = shortestPathFinder;
    }

    @Transactional
    public StationResponseDto create(String name) {
        checkName(name);
        final Station savedStation = stationRepository.save(Station.create(name));
        return stationAssembler.convertToDto(savedStation);
    }

    private void checkName(String name) {
        Assert.hasText(name, "name 은 필수 입니다.");
    }

    @Transactional(readOnly = true)
    public List<StationResponseDto> findAll() {
        return stationRepository.findAll().stream()
                .map(stationAssembler::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationResponseDto getStation(Long stationId) {
        final Station station = findById(stationId);

        return stationAssembler.convertToDto(station);
    }

    @Transactional(readOnly = true)
    public Station findById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 stationId 입니다. stationId : [" + stationId + "]"));
    }

    @Transactional
    public void delete(Long stationId) {
        Station station = findById(stationId);
        stationRepository.delete(station);
    }

    @Transactional(readOnly = true)
    public PathResponseDto getShortestPath(Long startStationId, Long endStationId) {
        final Station startStation = findById(startStationId);
        final Station endStation = findById(endStationId);
        return shortestPathFinder.findPath(startStation, endStation);
    }

}
