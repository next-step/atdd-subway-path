package atdd.station.service;

import atdd.station.model.dto.IdNameDto;
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

    public List<IdNameDto> lineDtos(List<Long> lineIds) {
        return lineRepository.findAllById(lineIds)
                .stream().map(data -> IdNameDto.builder()
                        .id(data.getId())
                        .name(data.getName()).build())
                .collect(Collectors.toList());
    }
}
