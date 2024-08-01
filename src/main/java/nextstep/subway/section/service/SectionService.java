package nextstep.subway.section.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.converter.SectionConverter.convertToSectionResponseByLineAndSection;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineService lineService;

    public SectionService(SectionRepository sectionRepository, StationService stationService, LineService lineService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.getLineByIdOrThrow(lineId);
        Sections sections = line.getSections();

        Station upStation = stationService.getStationByIdOrThrow(sectionRequest.getUpStationId());
        Station downStation = stationService.getStationByIdOrThrow(sectionRequest.getDownStationId());
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());

        sections.addSection(section);
        lineService.saveLine(line);

        return convertToSectionResponseByLineAndSection(line, section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineService.getLineByIdOrThrow(lineId);

        Sections sections = line.getSections();
        Section section = sections.getRemoveTargetSection(stationId);

        sections.removeSection(section);

        deleteSection(section);
        lineService.saveLine(line);
    }

    public void deleteSection(Section section) {
        sectionRepository.delete(section);
    }

}
