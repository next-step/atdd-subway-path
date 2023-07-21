package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Embeddable
public class Sections {
    private Long firstStationId;

    private Long lastStationId;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Long firstStationId, Long lastStationId) {
        validateStationId(firstStationId, lastStationId);

        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
    }

    private void validateStationId(Long firstStationId, Long lastStationId) {
        if (firstStationId.equals(lastStationId)) {
            throw new DuplicatedStationIdException();
        }
    }

    public void registerSection(Section newSection, Line line) {
        if (sections.isEmpty()) {
            addSection(newSection, line);
            return;
        }

        validateAlreadyRegisteredSection(newSection);

        if (newSection.downStationIdEqualsTo(firstStationId)) {
            firstStationId = newSection.getUpStationId();
            addSection(newSection, line);
            return;
        }

        if (newSection.upStationIdEqualsTo(lastStationId)) {
            lastStationId = newSection.getDownStationId();
            addSection(newSection, line);
            return;
        }

        registerSectionBetweenStations(newSection, line);
    }

    private void validateAlreadyRegisteredSection(Section newSection) {
        sections.stream()
                .filter(section -> section.hasAllSameStations(newSection))
                .findAny()
                .ifPresent(section -> {
                    throw new AlreadyRegisteredStationException();
                });
    }

    private void registerSectionBetweenStations(Section newSection, Line line) {
        Section existingSection = findExistingSection(newSection);

        if (existingSection.hasSameOrLongerDistance(newSection)) {
            throw new DistanceNotLongerThanExistingSectionException();
        }

        Section additionalSection;
        int additionalSectionDistance = existingSection.getDistance() - newSection.getDistance();

        if (existingSection.hasSameUpStation(newSection)) {
            additionalSection = new Section(newSection.getDownStation(), existingSection.getDownStation(), additionalSectionDistance);
        } else {
            additionalSection = new Section(existingSection.getUpStation(), newSection.getUpStation(), additionalSectionDistance);
        }

        sections.remove(existingSection);

        addSection(newSection, line);
        addSection(additionalSection, line);
    }

    private Section findExistingSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasOnlyOneSameStation(newSection))
                .findAny()
                .orElseThrow(InvalidSectionRegistrationException::new);
    }

    private void addSection(Section newSection, Line line) {
        sections.add(newSection);
        newSection.assignLine(line);
    }

    public List<Section> getSections() {
        List<Section> result = new ArrayList<>();
        Long targetStationId = firstStationId;
        while (targetStationId != null && !targetStationId.equals(lastStationId)) {
            for (Section section : sections) {
                if (section.upStationIdEqualsTo(targetStationId)) {
                    result.add(section);
                    targetStationId = section.getDownStationId();
                }
            }
        }

        return result;
    }

    public void deleteSection(Station station) {
        validateLineHasOnlyOneSection();
        validateStationIsDownStationOfLastSection(station);

        sections.remove(getLastSectionVer0());
    }

    private void validateLineHasOnlyOneSection() {
        if (hasOnlyOneSection()) {
            throw new CanNotDeleteOnlyOneSectionException();
        }
    }

    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    private void validateStationIsDownStationOfLastSection(Station station) {
        if (!getLastSectionVer0().downStationEqualsTo(station)) {
            throw new DeleteOnlyTerminusStationException();
        }
    }

    private Section getLastSectionVer0() {
        sections.sort(Comparator.comparing(Section::getId));
        return sections.get(sections.size() - 1);
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
    }
}
