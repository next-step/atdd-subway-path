package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.StationResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StationAssembler {

    @Transactional(readOnly = true)
    public StationResponseDto convertToDto(Station station) {
        return new StationResponseDto(station.getId(), station.getName());
    }

}
