package atdd.station.service;

import atdd.station.domain.Station;
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

    public StationService(StationAssembler stationAssembler, StationRepository stationRepository) {
        this.stationAssembler = stationAssembler;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponseDto create(String name) {
        checkName(name);
        final Station savedStation = stationRepository.save(new Station(name));
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
    public StationResponseDto getStation(Long id) {
        final Station station = stationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return stationAssembler.convertToDto(station);
    }

    @Transactional
    public void delete(Long id) {
        stationRepository.deleteById(id);
    }

}
