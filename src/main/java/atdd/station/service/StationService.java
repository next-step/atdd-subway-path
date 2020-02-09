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

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponseDto create(String name) {
        checkName(name);
        final Station savedStation = stationRepository.save(new Station(name));
        return StationResponseDto.from(savedStation);
    }

    private void checkName(String name) {
        Assert.hasText(name, "name 은 필수 입니다.");
    }

    @Transactional(readOnly = true)
    public List<StationResponseDto> findAll() {
        return stationRepository.findAll().stream().map(StationResponseDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationResponseDto getStation(String name) {
        checkName(name);
        final Station station = stationRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return StationResponseDto.from(station);
    }

    @Transactional
    public void delete(String name) {
        checkName(name);
        final int count = stationRepository.deleteByName(name);
        if (isNotDelete(count)) {
            throw new IllegalArgumentException("존재하지 않는 name 입니다. name = [" + name + "]");
        }
    }

    private boolean isNotDelete(int count) {
        return count <= 0;
    }

}
