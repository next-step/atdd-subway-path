package nextstep.subway.section;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineDataService;
import nextstep.subway.station.Station;
import nextstep.subway.station.service.StationDataService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class SectionService {

    private final LineDataService lineDataService;

    private final StationDataService stationDataService;

    public SectionService(LineDataService lineDataService, StationDataService stationDataService) {
        this.lineDataService = lineDataService;
        this.stationDataService = stationDataService;
    }

    public void saveSection(Long lineId, SectionCreateRequest request) {
        Line line = lineDataService.findLine(lineId);

        Station upStation = stationDataService.findStation(request.getUpStationId());
        Station downStation = stationDataService.findStation(request.getDownStationId());

        line.generateSection(request.getDistance(), upStation, downStation);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineDataService.findLine(lineId);

        Station station = stationDataService.findStation(stationId);

        line.deleteSection(station);
    }
}
