package nextstep.subway.application;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
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
        return convertToSectionResponses(convertConnectedSections(
                sectionRepository.findAllByLine(findLineById(lineId))));
    }

    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("노선 번호에 해당하는 노선이 없습니다."));
    }

    public List<Section> convertConnectedSections(List<Section> sections) {
        LinkedList<Section> linkedSections = new LinkedList<>();
        List<Section> sectionsToRemove = new ArrayList<>();

        Section firstSection = sections.get(0);
        linkedSections.add(firstSection);
        sectionsToRemove.add(firstSection);

        while (!sections.isEmpty()) {
            for (Section section : sections) {
                if (canConnectBefore(linkedSections.getFirst(), section)) {
                    linkedSections.addFirst(section);
                    sectionsToRemove.add(section);
                    break;
                }

                if (canConnectNext(linkedSections.getLast(), section)) {
                    linkedSections.addLast(section);
                    sectionsToRemove.add(section);
                }
            }
            sections.removeAll(sectionsToRemove);
        }
        return linkedSections;
    }

    private boolean canConnectBefore(Section targetSection, Section sectionToConnect) {
        return targetSection.canConnectBefore(sectionToConnect);
    }

    private boolean canConnectNext(Section targetSection, Section sectionToConnect) {
        return targetSection.canConnectNext(sectionToConnect);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("역 번호에 해당하는 역이 없습니다."));
    }

    private List<SectionResponse> convertToSectionResponses(List<Section> sections) {
        List<SectionResponse> responses = new ArrayList<>();

        sections.forEach(section -> responses.add(convertToSectionResponse(section)));
        return responses;
    }

    private SectionResponse convertToSectionResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                findStation(section.getUpStation().getId()),
                findStation(section.getDownStation().getId()),
                section.getDistance()
        );
    }
}
