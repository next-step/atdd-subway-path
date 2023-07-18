package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NoConnectedSectionException;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Line {
    private static final int FIRST_INDEX = 0;
    private static final int NEXT_VALUE = 1;
    private static final int MIN_DISTANCE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "POSITION")
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
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

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(Objects.requireNonNull(CollectionUtils.lastElement(sections)).getDownStation());
        return stations;
    }

    public boolean remove(Station station) {
        if (sections.isEmpty()) {
            return false;
        }
        int lastIndex = sections.size() - 1;
        if (!sections.get(lastIndex).getDownStation().equals(station)) {
            return false;
        }

        sections.remove(lastIndex);
        return true;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            addToLast(section);
            return;
        }
        validateSection(section);

        if (isAddToFirst(section)) {
            addToFirst(section);
            return;
        }
        if (isAddToLast(section)) {
            addToLast(section);
            return;
        }
        if (isAddToPrev(section)) {
            addToPrev(section);
            return;
        }
        if (isAddToNext(section)) {
            addToNext(section);
            return;
        }

        throw new IllegalStateException("Section is not match");
    }

    private void validateSection(Section section) {
        if (!isConnected(section)) {
            throw new NoConnectedSectionException();
        }
        if (isDuplicatedSection(section)) {
            throw new DuplicateSectionException();
        }
    }

    private boolean isConnected(Section section) {
        return sections.stream().anyMatch(s -> s.isConnected(section));
    }

    private boolean isDuplicatedSection(Section section) {
        return sections.stream().anyMatch(s -> s.isUpStation(section.getUpStation())) &&
                sections.stream().anyMatch(s -> s.isDownStation(section.getDownStation()));
    }


    private boolean isAddToFirst(Section section) {
        return sections.stream().anyMatch(s -> s.isUpStation(section.getDownStation()));
    }

    private boolean isAddToLast(Section section) {
        return sections.stream().anyMatch(s -> s.isDownStation(section.getUpStation()));
    }

    private boolean isAddToPrev(Section section) {
        return sections.stream().anyMatch(s -> s.isUpStation(section.getUpStation()));
    }

    private boolean isAddToNext(Section section) {
        return sections.stream().anyMatch(s -> s.isDownStation(section.getDownStation()));
    }

    private void addToFirst(Section section) {
        sections.add(FIRST_INDEX, section);
    }

    private void addToLast(Section section) {
        sections.add(section);
    }

    private void addToPrev(Section section) {
        Section registeredSection = getRegisterSection(section);
        int index = sections.indexOf(registeredSection);
        int distance = registeredSection.getDistance() - section.getDistance();
        validateDistance(distance);
        Section newSection = Section.of(registeredSection.getLine(), section.getDownStation(),
                registeredSection.getDownStation(), distance);
        sections.set(index, section);
        sections.add(index + NEXT_VALUE, newSection);
    }

    private void addToNext(Section section) {
        Section registeredSection = getRegisterSection(section);
        int index = sections.indexOf(registeredSection);
        int distance = registeredSection.getDistance() - section.getDistance();
        validateDistance(distance);
        Section newSection = Section.of(registeredSection.getLine(), registeredSection.getUpStation(),
                section.getUpStation(), distance);
        sections.set(index, newSection);
        sections.add(index + NEXT_VALUE, section);
    }

    private Section getRegisterSection(Section section) {
        return sections.stream().filter(s -> s.isContain(section)).findAny().get();
    }

    private void validateDistance(final int distance) {
        if (distance < MIN_DISTANCE) {
            throw new InvalidDistanceException();
        }
    }
}
