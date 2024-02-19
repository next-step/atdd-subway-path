package nextstep.subway.service;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Sections;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.response.SectionResponse;
import nextstep.subway.exception.ApplicationException;
import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationService stationService;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, LineRepository lineRepository, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    private boolean isRegisteredStation(List<Section> sections, Station station) {
        for (Section section : sections) {
            if(section.getUpStation().equals(station)){
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).get();
        Station station = stationService.findById(stationId);
        Sections sections = line.getSections();

        // 구간이 1개인 경우 삭제할 수 없다.
        if (sections.getSize() == 1) {
            throw new ApplicationException(ExceptionMessage.DELETE_ONLY_ONE_SECTION_EXCEPTION.getMessage());
        }

        line.deleteStation(station);
    }

    public SectionResponse findSectionByLineIdAndId(Long lineId, Long sectionId) {
        Section section = sectionRepository.findByLineIdAndId(lineId, sectionId);
        return createSectionResponse(section);
    }

    public SectionResponse createSectionResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }
}
