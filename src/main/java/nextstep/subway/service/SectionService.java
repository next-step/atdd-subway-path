package nextstep.subway.service;

import nextstep.subway.controller.dto.SectionAddRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Transactional(readOnly = true)
@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionAddRequest request) {
        Line line = findLineById(lineId);

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Section section = Section.of(
            line,
            upStation,
            downStation,
            request.getDistance()
        );

        line.addSection(section);
    }


    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);

        Section section = sectionRepository.findByLineIdAndDownStationId(lineId, stationId)
            .orElseThrow(EntityNotFoundException::new);

        line.removeSection(section);
    }

    private Line findLineById(Long lindId) {
        return lineRepository.findById(lindId)
            .orElseThrow(EntityNotFoundException::new);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(EntityNotFoundException::new);
    }
}
