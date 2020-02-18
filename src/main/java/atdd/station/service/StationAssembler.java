package atdd.station.service;

import atdd.line.domain.Line;
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

        return new StationResponseDto(station.getId(), station.getName(), convertLineDtos(station.getLines()));
    }

    private List<StationResponseDto.LineDto> convertLineDtos(List<Line> lines) {
        return lines.stream()
                .map(this::convertLineDto)
                .collect(Collectors.toList());
    }

    private StationResponseDto.LineDto convertLineDto(Line line) {
        return StationResponseDto.LineDto.of(line.getId(), line.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponseDto> convertToDtos(List<Station> stations) {
        return stations.stream().map(this::convertToDto).collect(Collectors.toList());
    }

}
