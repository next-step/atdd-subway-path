package nextstep.subway.section;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private StationService stationService;
    private LineService lineService;

    public SectionService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public Line save(long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineService.findLineById(lineId);

        Section stationSection = new Section(line, upStation, downStation, sectionRequest.getDistance());

        line.validateSaveSection(stationSection);

        lineService.addSection(line, stationSection);
        return line;
    }

    @Transactional
    public void deleteSectionByLineId(long lineId, long stationId) {
        Line line = lineService.findLineById(lineId);
        Station station = stationService.findById(stationId);

        line.validateDeleteSection(station);

        lineService.removeSection(line, station);
    }
}
