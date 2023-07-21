package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.*;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.*;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    private Long firstStationId;

    private Long lastStationId;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.firstStationId = section.getUpStationId();
        this.lastStationId = section.getDownStationId();

        addSection(section);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
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

    public void registerSection(Section newSection) {
        validateAlreadyRegisteredSection(newSection);

        if (newSection.downStationIdEqualsTo(firstStationId)) {
            firstStationId = newSection.getUpStationId();
            addSection(newSection);
            return;
        }

        if (newSection.upStationIdEqualsTo(lastStationId)) {
            lastStationId = newSection.getDownStationId();
            addSection(newSection);
            return;
        }

        registerSectionBetweenStations(newSection);
    }

    private void validateAlreadyRegisteredSection(Section newSection) {
        sections.stream()
                .filter(section -> section.hasAllSameStations(newSection))
                .findAny()
                .ifPresent(section -> {
                    throw new AlreadyRegisteredStationException();
                });
    }

    private void registerSectionBetweenStations(Section newSection) {
        Section existingSection = findExistingSection(newSection);

        if (existingSection.hasSameOrLongerDistance(newSection)) {
            throw new DistanceNotLongerThanExistingSectionException();
        }

        Section downSection;
        if (existingSection.hasSameUpStation(newSection)) {
            downSection = new Section(newSection.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - newSection.getDistance());
        } else {
            downSection = new Section(existingSection.getUpStation(), newSection.getUpStation(), existingSection.getDistance() - newSection.getDistance());
        }

        sections.remove(existingSection);

        addSection(newSection);
        addSection(downSection);
    }

    private Section findExistingSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasOnlyOneSameStation(newSection))
                .findAny()
                .orElseThrow(InvalidSectionRegistrationException::new);
    }

    private void addSection(Section newSection) {
        sections.add(newSection);
        newSection.assignLine(this);
    }
}
