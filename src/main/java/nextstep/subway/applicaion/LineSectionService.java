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
        createSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        return line;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.findById(lineId);
        createSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private void createSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        line.addSection(upStation, downStation, distance);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
