package nextstep.subway.application;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.SectionRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class SectionService {

    private final LineRepository lineRepository;

    private final SectionRepository sectionRepository;

    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public List<SectionResponse> findAllSectionsByLine(Long lineId) {
        List<Section> sections = sectionRepository.findAllByLine(findLineById(lineId));

        LinkedList<Section> linkedSections = new LinkedList<>();

        while (!sections.isEmpty()) {
            List<Section> sectionsToRemove = new ArrayList<>();

            for (Section section : sections) {
                // 초기 세팅
                if(linkedSections.isEmpty()) {
                    Section firstSection = sections.get(0);
                    linkedSections.add(firstSection);
                    sectionsToRemove.add(section);
                    break;
                }

                // linkedSections에서 가장 앞 Section의 upStation과 section의 downStation과 같다면 가장 앞에 연결
                if (linkedSections.getFirst().getUpStation().isSame(section.getDownStation())) {
                    linkedSections.addFirst(section);
                    sectionsToRemove.add(section);
                    break;
                }

                // linkedSection에서 마지막 Section의 downStation과 section의 upStation과 같다면 가장 뒤에 연결
                if (linkedSections.getLast().getDownStation().isSame(section.getUpStation())) {
                    linkedSections.addLast(section);
                    sectionsToRemove.add(section);
                    break;
                }
            }
            sections.removeAll(sectionsToRemove);
        }
        return convertToSectionResponses(linkedSections);
    }

    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("노선 번호에 해당하는 노선이 없습니다."));
    }

    private List<SectionResponse> convertToSectionResponses(List<Section> sections) {
        List<SectionResponse> responses = new ArrayList<>();

        sections.forEach(section -> responses.add(convertToSectionResponse(section)));
        return responses;
    }

    public SectionResponse convertToSectionResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                findStation(section.getUpStation().getId()),
                findStation(section.getDownStation().getId()),
                section.getDistance()
        );
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("역 번호에 해당하는 역이 없습니다."));
    }
}
