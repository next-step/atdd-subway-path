package nextstep.subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.domain.*;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    @Transactional
    public Long createSection(Long lineId, SectionCreateRequest request) {
        Line line = findLineBy(lineId);
        Sections sections = findSectionsBy(line);
        Stations stations = new Stations(stationRepository.findByIdIn(request.stationIds()));
        Station upStation = stations.findStationBy(request.getUpStationId());
        Station downStation = stations.findStationBy(request.getDownStationId());

        sections.validateRegisterStationBy(upStation, downStation);

        sections.addSectionInMiddle(upStation, downStation, request.getDistance());
        Section section = sectionRepository.save(
                new Section(line, upStation, downStation, request.getDistance())
        );

        return section.id();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineBy(lineId);
        Sections sections = findSectionsBy(line);
        Station deleteTargetStation = stationRepository.getBy(stationId);
        sections.validateDeleteSection();
        sections.findDeleteSectionAtTerminal(deleteTargetStation)
                .ifPresentOrElse(
                        sectionRepository::delete,
                        () -> {
                            Section prevSection = sections.findDeleteStationPrevSection(deleteTargetStation);
                            Section nextSection = sections.findDeleteStationNextSection(deleteTargetStation);
                            sectionRepository.deleteAll(List.of(prevSection, nextSection));
                            addSectionAfterDeleteSections(line, prevSection, nextSection);
                        }
                );
    }

    private void addSectionAfterDeleteSections(Line line, Section prevSection, Section nextSection) {
        sectionRepository.save(
                new Section(
                        line,
                        prevSection.upStation(),
                        nextSection.downStation(),
                        prevSection.distance() + nextSection.distance()
                )
        );
    }

    private Line findLineBy(Long lineId) {
        return lineRepository.getBy(lineId);
    }

    private Sections findSectionsBy(Line line) {
        return new Sections(sectionRepository.findByLine(line));
    }
}
