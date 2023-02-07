package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineSectionService {
    private final StationService stationService;

    public LineSectionService(StationService stationService) {
        this.stationService = stationService;
    }

    @Transactional
    public void addSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        line.addSection(new Section(upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Line line, Long stationId) {
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
