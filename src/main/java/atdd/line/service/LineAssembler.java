package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.dto.LineResponseDto;
import atdd.station.domain.Station;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LineAssembler {

    @Transactional(readOnly = true)
    public LineResponseDto convertToResponseDto(Line line) {

        return new LineResponseDto(line.getId(),
                line.getName(),
                line.getTimeTable(),
                line.getIntervalTime(),
                convertToStationDtos(line.getOrderedStations()));
    }

    private List<LineResponseDto.StationDto> convertToStationDtos(List<Station> stations) {
        return stations.stream()
                .map(this::convertToStationDto)
                .collect(Collectors.toList());
    }

    private LineResponseDto.StationDto convertToStationDto(Station station) {
        return LineResponseDto.StationDto.of(station.getId(), station.getName());
    }

}
