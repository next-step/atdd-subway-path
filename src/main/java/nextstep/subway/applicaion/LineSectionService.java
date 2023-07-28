package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LineSectionService {

    private final LineService lineService;
    private final StationService stationService;

    public LineSectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional
    public Line saveLineWithSection(LineRequest request) {
        Line line = lineService.save(request);
        createSection(request.getUpStationId(), request.getDownStationId(), line, request.getDistance());
        return line;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.findById(lineId);
        createSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), line, sectionRequest.getDistance());
    }

    private void createSection(Long upStationId, Long downStationId, Line line, int request2) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        line.addSection(upStation, downStation, request2);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
