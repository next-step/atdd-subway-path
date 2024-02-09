package nextstep.subway.service;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Sections;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.SectionRequest;
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

    @Transactional
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).get();
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        // 새로운 구간의 상행역이 등록된 노선의 하행 종점역이 아니면 에러
        sectionValidation(line, upStation, downStation);

        Section newSection = new Section(line, upStation, downStation, sectionRequest.getDistance());
        line.addSection(newSection);
        newSection = sectionRepository.save(newSection);
        return createSectionResponse(newSection);
    }

    private void sectionValidation(Line line, Station upStation, Station downStation) {
        Sections sections = line.getSections();

        if (sections.getSections().isEmpty()) {
            return;
        }

        Section section = sections.getLastSection();
        if (!section.getDownStation().equals(upStation)) {
            throw new ApplicationException(ExceptionMessage.UPSTATION_VALIDATION_EXCEPTION.getMessage());
        }

        // 새로운 구간의 하행역이 노선에 등록되어있는 역과 같으면 에러
        if (isRegisteredStation(sections.getSections(), downStation)) {
            throw new ApplicationException(ExceptionMessage.DOWNSTATION_VALIDATION_EXCEPTION.getMessage());
        }

        // 새로운 구간의 상행역과 하행역이 같으면 에러
        if (upStation.equals(downStation)) {
            throw new ApplicationException(ExceptionMessage.NEW_SECTION_VALIDATION_EXCEPTION.getMessage());
        }
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
        Sections sections = line.getSections();
        Section lastSection = sections.getLastSection(); // 마지막 구간

        // stationId 는 마지막 하행 종착역 이어야 한다.
        if (!stationId.equals(lastSection.getDownStation().getId())) {
            throw new ApplicationException(ExceptionMessage.DELETE_LAST_SECTION_EXCEPTION.getMessage());
        }

        // 구간이 1개인 경우 삭제할 수 없다.
        if (sections.getSize() == 1) {
            throw new ApplicationException(ExceptionMessage.DELETE_ONLY_ONE_SECTION_EXCEPTION.getMessage());
        }

        line.deleteSection(lastSection);
    }

    public SectionResponse findSectionByLineIdAndId(Long lineId, Long sectionId) {
        Section section = sectionRepository.findByLineIdAndId(lineId, sectionId);
        return createSectionResponse(section);
    }

    public SectionResponse createSectionResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }
}
