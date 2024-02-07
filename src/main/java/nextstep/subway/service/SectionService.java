package nextstep.subway.service;

import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(
        LineService lineService,
        StationService stationService
    ) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    /** 구간을 생성한다. */
    public Long createSection(Long lineId, SectionRequest request) {
        Line line = lineService.findLine(lineId);
        Station downStation = stationService.findStation(request.getDownStationId());
        Station upStation = stationService.findStation(request.getUpStationId());

        Section section = new Section(line, downStation, upStation, request.getDistance());
        line.addSection(section);

        return section.getId();
    }

    /** 구간을 삭제한다. */
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.findLine(lineId);
        Station station = stationService.findStation(stationId);

        line.removeSection(station);
    }
}
