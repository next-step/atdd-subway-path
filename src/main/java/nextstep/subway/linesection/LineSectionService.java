package nextstep.subway.linesection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineSectionService {
    private final LineService lineService;
    private final StationService stationService;

    public LineSectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }


    @Transactional
    public void addSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Line line = lineService.getLine(lineId);
        Station upStation = stationService.getStation(upStationId);
        Station downStation = stationService.getStation(downStationId);
        line.addSection(LineSection.of(line, upStation, downStation, distance));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.getLine(lineId);
        Station toDeleteStation = stationService.getStation(stationId);
        line.removeSection(toDeleteStation);
    }
}
