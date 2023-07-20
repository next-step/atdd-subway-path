package nextstep.subway.domain;

import nextstep.subway.exception.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int NEXT_VALUE = 1;
    private static final int MIN_DISTANCE = 1;
    private static final int MIN_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "POSITION")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
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

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            addToLast(section);
            return;
        }
        validateAddSection(section);

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

    public void remove(Station station) {
        System.out.println("station : " + station);
        System.out.println("sections : " + sections);
        validateRemoveStation(station);

        if (isFirst(station)) {
            removeFirstStation();
            return;
        }
        if (isLast(station)) {
            removeLastStation();
            return;
        }

        removeMiddleStation(station);
    }

    private void validateAddSection(Section section) {
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
        return sections.stream().filter(s -> s.isContains(section)).findAny().get();
    }

    private void validateDistance(final int distance) {
        if (distance < MIN_DISTANCE) {
            throw new InvalidDistanceException();
        }
    }

    private void validateRemoveStation(Station station) {
        if (sections.size() <= MIN_SECTIONS_SIZE) {
            throw new LastSectionException();
        }
        if (sections.stream().noneMatch(s -> s.isContains(station))) {
            throw new NotFoundStationException();
        }
    }

    private boolean isFirst(Station station) {
        return sections.get(FIRST_INDEX).isUpStation(station);
    }

    private boolean isLast(Station station) {
        return Objects.requireNonNull(CollectionUtils.lastElement(sections)).isDownStation(station);
    }

    private void removeFirstStation() {
        sections.remove(FIRST_INDEX);
    }

    private void removeLastStation() {
        int lastIndex = sections.size() - 1;
        sections.remove(lastIndex);
    }

    private void removeMiddleStation(Station station) {
        Section section = getSection(station);
        int index = sections.indexOf(section);
        Section nextSection = sections.get(index + NEXT_VALUE);
        int distance = section.getDistance() + nextSection.getDistance();
        sections.set(index, Section.of(section.getLine(), section.getUpStation(), nextSection.getDownStation(), distance));
        sections.remove(nextSection);
    }

    private Section getSection(Station station) {
        return sections.stream().filter(s -> s.isDownStation(station)).findAny().get();
    }
}
