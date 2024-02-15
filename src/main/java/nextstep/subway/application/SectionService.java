package nextstep.subway.application;

import nextstep.subway.dto.SectionResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class SectionService {

    private final LineService lineService;

    private final SectionRepository sectionRepository;

    public SectionService(LineService lineService, SectionRepository sectionRepository) {
        this.lineService = lineService;
        this.sectionRepository = sectionRepository;
    }

    public List<SectionResponse> findAllSectionsByLine(Long lineId) {
        List<Section> sections = sectionRepository.findAllByLine(lineService.findLineById(lineId));

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

    private List<SectionResponse> convertToSectionResponses(List<Section> sections) {
        List<SectionResponse> responses = new ArrayList<>();

        sections.forEach(section -> responses.add(lineService.convertToSectionResponse(section)));
        return responses;
    }
}
