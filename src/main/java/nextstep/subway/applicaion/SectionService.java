package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = lineRepository.findById(lineId)
                                          .orElseThrow(IllegalArgumentException::new)
                                          .getSections();
        sections.removeSection(stationId);
    }
}
