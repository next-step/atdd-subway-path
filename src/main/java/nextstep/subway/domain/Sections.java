package nextstep.subway.domain;

import static nextstep.subway.exception.ErrorCode.DUPLICATED_SECTION;
import static nextstep.subway.exception.ErrorCode.INVALID_DISTANCE;
import static nextstep.subway.exception.ErrorCode.INVALID_SECTION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.SubwayException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        int MIN_SECTION_SIZE_TO_SORT = 2;
        if (sections.size() >= MIN_SECTION_SIZE_TO_SORT) {
            return sortSectionsByOrder(sections);
        }
        return sections;
    }

    private List<Section> sortSectionsByOrder(List<Section> sections) {
        Station upFirstStation = getUpFirstStation(sections);
        Section firstSection = getSectionByUpStation(sections, upFirstStation);

        List<Section> sortedSections = new ArrayList<>();
        Section currentSection = firstSection;
        while (currentSection != null) {
            sortedSections.add(currentSection);
            Station nextUpStation = currentSection.getDownStation();
            currentSection = getSectionByUpStation(sections, nextUpStation);
        }
        return sortedSections;
    }

    private Station getUpFirstStation(List<Section> sections) {
        return sections
            .stream()
            .map(Section::getUpStation)
            .filter(upStation -> sections
                .stream()
                .noneMatch(section -> upStation.equals(section.getDownStation())))
            .findFirst()
            .orElse(null);
    }

    private Section getSectionByUpStation(List<Section> sections, Station upStation) {
        return sections
            .stream()
            .filter(section -> upStation.equals(section.getUpStation()))
            .findFirst()
            .orElse(null);
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateSectionAddition(newSection);

        if (canAddSectionBetween(newSection)) {
            addSectionBetween(newSection);
            return;
        }

        if (canAddSectionAsEndpoint(newSection)) {
            sections.add(newSection);
        }
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        addSection(new Section(line, upStation, downStation, distance));
    }

    private void validateSectionAddition(Section section) {
        boolean upStationNotInLine = !isStationInLine(section.getUpStation());
        boolean downStationNotInLine = !isStationInLine(section.getDownStation());
        if (upStationNotInLine && downStationNotInLine) {
            throw new SubwayException(INVALID_SECTION);
        }
    }

    private boolean isStationInLine(Station station) {
        return getStations().contains(station);
    }

    public List<Station> getStations() {
        return getSections()
            .stream()
            .flatMap(section ->
                Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    private boolean canAddSectionBetween(Section newSection) {
        return sections.stream()
            .anyMatch(section ->
                section.getUpStation().equals(newSection.getUpStation()));
    }

    private void addSectionBetween(Section section) {
        Section baseSection = getBaseSection(section);
        validateDistance(baseSection, section);
        validateDuplicateSection(baseSection, section);
        addSectionToBaseSection(baseSection, section);
    }

    private boolean canAddSectionAsEndpoint(Section newSection) {
        return canAddSectionAsUpwardEndpoint(newSection)
            || canAddSectionAsDownwardEndpoint(newSection);
    }

    private boolean canAddSectionAsUpwardEndpoint(Section newSection) {
        return sections.stream()
            .anyMatch(section ->
                newSection.getDownStation().equals(section.getUpStation()));
    }

    private boolean canAddSectionAsDownwardEndpoint(Section newSection) {
        return sections.stream()
            .anyMatch(section ->
                newSection.getUpStation().equals(section.getDownStation()));
    }

    private Section getBaseSection(Section section) {
        return sections.stream()
            .filter(s -> section.getUpStation().equals(s.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new SubwayException(INVALID_SECTION));
    }

    private void validateDistance(Section baseSection, Section section) {
        if (baseSection.getDistance() <= section.getDistance()) {
            throw new SubwayException(INVALID_DISTANCE);
        }
    }

    private void validateDuplicateSection(Section baseSection, Section section) {
        if (baseSection.getDownStation() == section.getDownStation()) {
            throw new SubwayException(DUPLICATED_SECTION);
        }
    }

    private void addSectionToBaseSection(Section baseSection, Section section) {
        Section newSection = new Section(
            baseSection.getLine(),
            section.getDownStation(),
            baseSection.getDownStation(),
            baseSection.getDistance() - section.getDistance());

        sections.remove(baseSection);
        sections.add(section);
        sections.add(newSection);
    }


    public void deleteSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int lastSectionIndex = sections.size() - 1;
        Section lastSection = getSections().get(lastSectionIndex);
        Station lastSectionDownStation = lastSection.getDownStation();
        if (!lastSectionDownStation.equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(lastSectionIndex);
    }
}
