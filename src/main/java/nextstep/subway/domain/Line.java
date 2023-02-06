package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.exception.NotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            addSectionWhenExistingSectionsIsEmpty(section);
            return;
        }
        validateSection(section);

        // 상행역이 같으며 사이에 삽입하는 경우
        if (isInsertSectionIntoExistingSectionWithSameUpStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            Section newSection1 = new Section(this, section.getUpStation(), section.getDownStation(), section.getDistance());
            Section newSection2 = new Section(this, section.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - section.getDistance());

            sections.remove(existingSection);
            sections.addAll(List.of(newSection1, newSection2));
            return;
        }

        // 하행역이 같으며 사이에 삽입하는 경우
        if (isInsertSectionIntoExistingSectionWithSameDownStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            Section newSection1 = new Section(this, existingSection.getUpStation(), section.getUpStation(), existingSection.getDistance() - section.getDistance());
            Section newSection2 = new Section(this, section.getUpStation(), section.getDownStation(), section.getDistance());

            sections.remove(existingSection);
            sections.addAll(List.of(newSection1, newSection2));
            return;
        }

        sections.add(section);
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

    private Section getSectionByUpStation(Station upStation) {
        return sections.stream().filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Section not found."));
    }

    private boolean isContainingAtLeastOneEndSection(Section section) {
        return isSectionContainsStation(section.getUpStation()) || isSectionContainsStation(section.getDownStation());
    }

    private boolean isDistanceShorterThanExistingSection(Section section) {
        return getFirstSection().getDistance() > section.getDistance();
    }

    private boolean isInsertSectionIntoExistingSection(Section section) {
        return isInsertSectionIntoExistingSectionWithSameDownStation(section) ||
                isInsertSectionIntoExistingSectionWithSameUpStation(section);
    }

    private boolean isInsertSectionIntoExistingSectionWithSameDownStation(Section section) {
        return (!getUpStation().equals(section.getUpStation()) && getDownStation().equals(section.getDownStation()));
    }

    private boolean isInsertSectionIntoExistingSectionWithSameUpStation(Section section) {
        return (getUpStation().equals(section.getUpStation()) && !getDownStation().equals(section.getDownStation()));
    }

    private boolean isSectionContainsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    private Station getUpStation() {
        return getFirstSection().getUpStation();
    }

    private Section getFirstSection() {
        // find upStation that is not downStation of other section.
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.isDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("UpStation not found."));
    }

    private Station getDownStation() {
        return getLastSection().getDownStation();
    }

    private Section getLastSection() {
        // find downStation that is not upStation of other section.
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.isUpStation(section.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("DownStation not found."));
    }

    public static final int MIN_SECTION_SIZE_OF_LINE = 1;

    public void removeSection(Station deleteStation) {
        if (sections.size() == MIN_SECTION_SIZE_OF_LINE) {
            throw new CannotDeleteSectionException("Line has only 1 section");
        }
        if (!getLastSection().isDownStation(deleteStation)) {
            throw new CannotDeleteSectionException("Station that trying to delete is not downStation of this line.");
        }
        sections.remove(getLastSection());
    }
}
