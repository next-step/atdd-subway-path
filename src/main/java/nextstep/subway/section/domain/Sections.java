package nextstep.subway.section.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    private final static int FIRST = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
        }

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    public List<Section> getSortedSections() {
        Section firstSection = getFirstSection();

        List<Section> sortedSections =
                Stream.iterate(firstSection, section -> sections.stream()
                                .filter(nextSection -> section.getDownStation().equals(nextSection.getUpStation()))
                                .findFirst()
                                .orElse(null))
                        .takeWhile(Objects::nonNull)
                        .collect(Collectors.toList());

        return sortedSections;
    }

    private Section getFirstSection() {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }

        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(nextSection -> section.getUpStation().equals(nextSection.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundSectionException());
    }

    private Section getLastSection() {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }

        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(nextSection -> section.getDownStation().equals(nextSection.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new NotFoundSectionException());
    }

    public void addSection(Section newSection) {
        validateAddSection(newSection);

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        if (possibleAddedFirstSection(newSection)) {
            addFirstSection(newSection);
            return;
        }
        if (possibleAddedLastSection(newSection)) {
            addLastSection(newSection);
            return;
        }
        addMiddleSection(newSection);
    }

    private void validateAddSection(Section newSection) {
        if (this.sections.isEmpty()) {
            return;
        }
        if (this.sections.contains(newSection)) {
            throw new AlreadyExistSectionException();
        }

        List<Station> stations = getStations();

        if (!stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation())) {
            throw new NotFoundUpStationOrDownStation();
        }
    }

    private void addFirstSection(Section newSection) {
        if (!possibleAddedFirstSection(newSection)) {
            return;
        }
        this.sections.add(newSection);
    }

    private void addLastSection(Section newSection) {
        if (!possibleAddedLastSection(newSection)) {
            return;
        }
        this.sections.add(newSection);
    }

    private void addMiddleSection(Section newSection) {
        if (possibleAddedFirstSection(newSection) && possibleAddedLastSection(newSection)) {
            return;
        }

        Section section = sections.stream()
                .filter(s -> s.isUpStation(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundStationException());

        section.updateUpStation(newSection.getDownStation());
        section.reduceDistance(newSection.getDistance());
        this.sections.add(newSection);
    }

    private boolean possibleAddedFirstSection(Section targetSection) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getFirstSection().getUpStation().equals(targetSection.getDownStation());
    }

    private boolean possibleAddedLastSection(Section targetSection) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getLastSection().getDownStation().equals(targetSection.getUpStation());
    }

    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new NotFoundStationException();
        }
        if (isNotLastStation(station)) {
            throw new IsNotLastStationException();
        }
        if (size() == 1) {
            throw new DeleteSectionException();
        }

        Section lastSection = getLastSection();

        lastSection.delete();
        this.sections.remove(lastSection);
    }

    private boolean isNotLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return !getLastSection().isDownStation(station);
    }

    public boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> getSections() {
        return this.sections;
    }
}
