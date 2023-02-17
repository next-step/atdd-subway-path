package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private final StationService stationService;
    private final LineService lineService;

    public SectionService(final StationService stationService, final LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineService.findLineById(lineId);

        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = lineService.findLineById(lineId);
        Station station = stationService.findById(stationId);

        line.removeSection(station);
    }
}
