package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.StationResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StationAssembler {

    @Transactional(readOnly = true)
    public StationResponseDto convertToDto(Station station) {
        return new StationResponseDto(station.getId(), station.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponseDto> convertToDtos(List<Station> stations) {
        return stations.stream().map(this::convertToDto).collect(Collectors.toList());
    }

}
