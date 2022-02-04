package nextstep.subway.domain;

import nextstep.subway.exception.*;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validateAddSection(section);

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        Optional<Section> betweenDownStation = findSectionEqualsUpStation(upStation);

        if (betweenDownStation.isPresent()) {
            addBetweenDownStationSection(section, downStation, betweenDownStation.get());
            return;
        }

        Optional<Section> betweenUpStation = findSectionEqualsDownStation(downStation);
        if (betweenUpStation.isPresent()) {
            addBetweenUpStationSection(section, upStation, betweenUpStation.get());
            return;
        }

        this.sections.add(section);
    }

    private void addBetweenUpStationSection(Section section, Station upStation, Section existSection) {
        int existSectionDistance = existSection.getDistance();

        if (section.isNotValidateSectionDistance(existSectionDistance)) {
            throw new SectionDistanceNotValidException();
        }

        existSection.changeDownStationDistance(upStation, existSectionDistance - section.getDistance());
        section.changeDistance(existSectionDistance - existSection.getDistance());

        this.sections.add(section);
    }

    private void addBetweenDownStationSection(Section section, Station downStation, Section existSection) {
        Station existDownStation = existSection.getDownStation();
        int existSectionDistance = existSection.getDistance();

        if (section.isNotValidateSectionDistance(existSectionDistance)) {
            throw new SectionDistanceNotValidException();
        }

        existSection.changeDownStationDistance(downStation, section.getDistance());
        section.changeStationDistance(downStation, existDownStation, existSectionDistance - section.getDistance());

        this.sections.add(section);
    }

    public List<Section> getAllSections() {
        return sections;
    }

    public Station getLastDownStation() {
        return sections
            .stream()
            .map(Section::getDownStation)
            .reduce((first, second) -> second)
            .orElseThrow(() -> new StationNotFoundException(ErrorMessages.SECTION_NOT_FOUND_LAST_DOWN_STATION.getMessage()));
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> downStations = getAllDownStations();
        Section section = findFirstSection(downStations);

        List<Station> allStations = new ArrayList<>();
        allStations.add(section.getUpStation());

        Optional<Section> nextSection = findSectionEqualsUpStation(section.getDownStation());

        while (nextSection.isPresent()) {
            section = nextSection.get();
            nextSection = findSectionEqualsUpStation(section.getDownStation());
            allStations.add(section.getUpStation());
        }

        allStations.add(section.getDownStation());

        return allStations;
    }

    public void removeSection(Station station) {
        Station lastDownStation = getLastDownStation();

        if (isNotAvailableDelete()) {
            throw new DeleteSectionException(ErrorMessages.DELETE_NOT_AVAILABLE_SECTION.getMessage());
        }

        if (!lastDownStation.equals(station)) {
            throw new DeleteSectionException(ErrorMessages.SECTION_NOT_FOUND_LAST_DOWN_STATION.getMessage());
        }

        Section delete = getByDownStation(lastDownStation);

        sections.remove(delete);
    }

    public Section getByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst()
            .orElseThrow(SectionNotFoundException::new);
    }

    private void validateAddSection(Section section) {
        if (hasSameSection(section)) {
            throw new DuplicateSectionException();
        }

        if (isNotValidateSection(section)) {
            throw new SectionValidException();
        }
    }

    private boolean isNotValidateSection(Section section) {
        if (sections.isEmpty()) {
            return false;
        }

        List<Station> allStations = getAllStations();

        boolean containsUpStation = allStations.contains(section.getUpStation());
        boolean containsDownStation = allStations.contains(section.getDownStation());

        return !(containsUpStation || containsDownStation);
    }

    private boolean hasSameSection(Section section) {
        return sections
            .stream()
            .anyMatch(it -> it.isSameSection(section));
    }

    private Optional<Section> findSectionEqualsUpStation(Station station) {
        return sections
            .stream()
            .filter(it -> it.getUpStation().equals(station))
            .findFirst();
    }

    private Optional<Section> findSectionEqualsDownStation(Station station) {
        return sections
            .stream()
            .filter(it -> it.getDownStation().equals(station))
            .findFirst();
    }

    private Section findFirstSection(List<Station> downStations) {
        return sections
            .stream()
            .filter(it -> !downStations.contains(it.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new SectionNotFoundException(ErrorMessages.SECTION_NOT_FOUND_FIRST_UP_STATION.getMessage()));
    }

    private List<Station> getAllDownStations() {
        return sections.
            stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    private boolean isNotAvailableDelete() {
        return sections.size() <= MIN_SECTION_SIZE;
    }
}
