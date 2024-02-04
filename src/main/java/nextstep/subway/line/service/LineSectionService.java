package nextstep.subway.line.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.service.StationDataService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class LineSectionService {
    private final LineDataService lineDataService;

    private final StationDataService stationDataService;

    public LineSectionService(LineDataService lineDataService, StationDataService stationDataService) {
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
