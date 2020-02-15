package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import atdd.line.dto.LineResponseDto;
import atdd.station.service.StationAssembler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LineAssembler {

    private final StationAssembler stationAssembler;

    public LineAssembler(StationAssembler stationAssembler) {
        this.stationAssembler = stationAssembler;
    }

    @Transactional(readOnly = true)
    public LineResponseDto convertToResponseDto(Line line) {

        final TimeTable timeTable = line.getTimeTable();

        return new LineResponseDto(line.getId(), line.getName(), timeTable, line.getIntervalTime(), stationAssembler.convertToDtos(line.getStations()));
    }

}
