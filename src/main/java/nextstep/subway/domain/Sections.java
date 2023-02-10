package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getAllStations() {
        // list of stations that first is upStation and last is downStation
        Section firstSection = getFirstSection();
        Section lastSection = getLastSection();

        // order all stations by firstSection and lastSection
        LinkedList<Station> stations = new LinkedList<>();
        while (true) {
            stations.add(firstSection.getUpStation());
            stations.add(firstSection.getDownStation());
            if (firstSection.equals(lastSection)) {
                break;
            }
            firstSection = getSectionByUpStation(firstSection.getDownStation());
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }

    public void addSection(Line line, Section section) {
        if (sections.isEmpty()) {
            addSectionWhenExistingSectionsIsEmpty(section);
            return;
        }
        validateSection(section);

        // 상행역이 같으며 사이에 삽입하는 경우
        if (addableAfterUpStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            Section newSection1 = new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance());
            Section newSection2 = new Section(line, section.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - section.getDistance());

            sections.remove(existingSection);
            sections.addAll(List.of(newSection1, newSection2));
            return;
        }

        // 하행역이 같으며 사이에 삽입하는 경우
        if (addableBeforeDownStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            Section newSection1 = new Section(line, existingSection.getUpStation(), section.getUpStation(), existingSection.getDistance() - section.getDistance());
            Section newSection2 = new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance());

            sections.remove(existingSection);
            sections.addAll(List.of(newSection1, newSection2));
            return;
        }

        sections.add(section);
    }

    private boolean addableBeforeDownStation(Section section) {
        return (!getUpStation().equals(section.getUpStation()) && getDownStation().equals(section.getDownStation()));
    }

    private boolean addableAfterUpStation(Section section) {
        return (getUpStation().equals(section.getUpStation()) && !getDownStation().equals(section.getDownStation()));
    }

    private void addSectionWhenExistingSectionsIsEmpty(Section section) {
        if (Objects.equals(section.getUpStation(), section.getDownStation())) {
            throw new BadRequestException("UpStation and DownStation are same.");
        }

        sections.add(section);
    }

    private void validateSection(Section section) {
        if (isSectionContainsStation(section.getUpStation()) && isSectionContainsStation(section.getDownStation())) {
            throw new BadRequestException("Both stations are already registered.");
        }

        if (isInsertSectionIntoExistingSection(section) && !isDistanceShorterThanExistingSection(section)) {
            throw new BadRequestException("Distance must be less than existing section's distance.");
        }

        if (!isContainingAtLeastOneEndSection(section)) {
            throw new BadRequestException("Neither UpStation nor DownStation is registered in the line.");
        }
    }

    private boolean isContainingAtLeastOneEndSection(Section section) {
        return isSectionContainsStation(section.getUpStation()) || isSectionContainsStation(section.getDownStation());
    }

    private boolean isDistanceShorterThanExistingSection(Section section) {
        return getFirstSection().getDistance() > section.getDistance();
    }

    private boolean isInsertSectionIntoExistingSection(Section section) {
        return addableBeforeDownStation(section) ||
                addableAfterUpStation(section);
    }

    private boolean isSectionContainsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }


    private Station getUpStation() {
        return getFirstSection().getUpStation();
    }


    private Station getDownStation() {
        return getLastSection().getDownStation();
    }

    public Section getFirstSection() {
        // find upStation that is not downStation of other section.
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.isDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("UpStation not found."));
    }

    public Section getLastSection() {
        // find downStation that is not upStation of other section.
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.isUpStation(section.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("DownStation not found."));
    }

    public Section getSectionByUpStation(Station upStation) {
        return sections.stream().filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Section not found."));
    }

    private Section getSectionByDownStation(Station station) {
        return sections.stream().filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Section not found."));
    }

    public int size() {
        return sections.size();
    }

    private static final int MIN_SECTION_SIZE_OF_LINE = 1;

    public void removeSection(Station deleteStation) {
        validateCanRemoveSection(deleteStation);

        if (isUpStation(deleteStation)) {
            sections.remove(getSectionByUpStation(deleteStation));
            return;
        }

        if (isDownStation(deleteStation)) {
            sections.remove(getSectionByDownStation(deleteStation));
            return;
        }

        Section upSection = getSectionByDownStation(deleteStation);
        Section downSection = getSectionByUpStation(deleteStation);

        Section mergedSection = new Section(
                upSection.getLine(),
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance()
        );

        sections.add(mergedSection);
        sections.removeAll(List.of(upSection, downSection));
    }

    private void validateCanRemoveSection(Station deleteStation) {
        if (sections.isEmpty() || isNotContainStation(deleteStation)) {
            throw new CannotDeleteSectionException("Station is not registered in the line.");
        }

        if (sections.size() == MIN_SECTION_SIZE_OF_LINE) {
            throw new CannotDeleteSectionException("Line has only 1 section");
        }
    }

    private boolean isNotContainStation(Station deleteStation) {
        return !getAllStations().contains(deleteStation);
    }

    private boolean isUpStation(Station station) {
        return getUpStation().equals(station);
    }

    private boolean isDownStation(Station station) {
        return getDownStation().equals(station);
    }
}
