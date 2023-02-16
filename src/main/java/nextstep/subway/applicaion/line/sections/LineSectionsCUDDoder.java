package nextstep.subway.applicaion.line.sections;

import nextstep.subway.applicaion.station.StationService;
import nextstep.subway.applicaion.dto.section.SectionRequest;
import nextstep.subway.applicaion.line.LineService;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineSectionsCUDDoder extends LineService {
    public LineSectionsCUDDoder(LineRepository lineRepository, StationRepository stationRepository) {
        super(lineRepository, stationRepository);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(IllegalArgumentException::new);
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

        line.addSection(Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(sectionRequest.getDistance()))
                .build());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station deleteStation = stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);

        line.removeSection(deleteStation);
    }
}
