package atdd.station.service;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import atdd.station.model.dto.LineSimpleDto;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import atdd.station.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    public Station findById(long id) {
        return stationRepository.findById(id).orElseThrow(() -> new SubwayException(ErrorType.NOT_FOUND_STATION));
    }

    public List<Station> findAllById(Iterable<Long> ids) {
        return stationRepository.findAllById(ids);
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<Station> saveAll(Iterable<Station> stations) {
        return stationRepository.saveAll(stations);
    }

    // TODO 수정
    public List<LineSimpleDto> lineDtos(List<Long> lineIds) {
        return lineRepository.findAllById(lineIds)
                .stream().map(data -> LineSimpleDto.builder()
                        .id(data.getId())
                        .name(data.getName()).build())
                .collect(Collectors.toList());
    }
}
