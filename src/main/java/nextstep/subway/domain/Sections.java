package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.AlreadyRegisterStationException;
import nextstep.subway.exception.CannotRegisterSectionException;

@Embeddable
public class Sections {

    @OneToMany(cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(upStation, downStation, distance));
            return;
        }
        validationRegisteredStations(upStation, downStation);

        if (registerSectionIsFirst(downStation)) {
            sections.add(0, new Section(upStation, downStation, distance));
            return;
        }

        if (registerSectionIsLast(upStation)) {
            sections.add(sections.size() - 1, new Section(upStation, downStation, distance));
            return;
        }

        addSectionAtMiddle(upStation, downStation, distance);
    }

    private void addSectionAtMiddle(Station upStation, Station downStation, int distance) {
        if (isNewStation(upStation)) {
            Section section = findSectionIncludeDownStation(downStation);
            validateDistance(section.getDistance(), distance);
            sections.remove(section);
            sections.add(new Section(section.getUpStation(), upStation, section.getDistance() - distance));
            sections.add(new Section(upStation, downStation, distance));
            return;
        }

        if (isNewStation(downStation)) {
            Section section = findSectionIncludeUpStation(upStation);
            validateDistance(section.getDistance(), distance);
            sections.remove(section);
            sections.add(new Section(upStation, downStation, distance));
            sections.add(new Section(downStation, section.getDownStation(), section.getDistance() - distance));
            return;
        }
        throw new CannotRegisterSectionException();
    }

    private void validateDistance(int distance, int registerDistance) {
        if (distance <= registerDistance) {
            throw new CannotRegisterSectionException();
        }
    }

    private Section findSectionIncludeUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow(CannotRegisterSectionException::new);
    }

    private Section findSectionIncludeDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(CannotRegisterSectionException::new);
    }

    private boolean isNewStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.containStation(station));
    }

    private boolean registerSectionIsLast(Station upStation) {
        Section lastSection = lastSection();
        return lastSection.getDownStation().equals(upStation);
    }

    private Section lastSection() {
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.getUpStation().equals(section.getDownStation())))
                .findFirst()
                .orElseThrow(CannotRegisterSectionException::new);
    }

    private boolean registerSectionIsFirst(Station downStation) {
        Section firstSection = firstSection();
        return firstSection.getUpStation().equals(downStation);
    }

    private Section firstSection() {
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> section.getUpStation().equals(otherSection.getDownStation())))
                .findFirst()
                .orElseThrow(CannotRegisterSectionException::new);
    }

    private void validationRegisteredStations(Station upStation, Station downStation) {
        if (getStations().contains(upStation) && getStations().contains(downStation)) {
            throw new CannotRegisterSectionException();
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Section currentSection = firstSection();
        stations.add(currentSection.getUpStation());

        while (hasNext(currentSection)) {
            currentSection = nextSection(currentSection);
            stations.add(currentSection.getUpStation());
        }
        stations.add(currentSection.getDownStation());
        return stations;
    }

    private Section nextSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(currentSection.getDownStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean hasNext(Section currentSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(currentSection.getDownStation()));
    }

    public void removeSection() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }
}
