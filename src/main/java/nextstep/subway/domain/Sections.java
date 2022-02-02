package nextstep.subway.domain;

import nextstep.subway.exception.section.AlreadyRegisteredStationException;
import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.MinimumSectionException;
import nextstep.subway.exception.section.NotFoundConnectStationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private static final int NEXT_VALUE = 1;
    private static final int FIRST_INDEX = 0;
    private static final int MINIMUM_SIZE_SECTION = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (isEmpty()) {
            sections.add(section);
            return;
        }

        validateAddSection(section);

        if (isRegisteredUpStation(section)) {
            addUpSection(section);
            return;
        }

        addDownSection(section);
    }

    public void deleteStation(Station station) {
        validateMinimumSection();

        if (isEndStation(station)) {
            deleteEndStation(station);
            return;
        }

        deleteMiddleStation(station);
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(getLastDownStation());

        return stations;
    }

    private void addUpSection(Section section) {
        Section findSection = getRegisteredUpStation(section);
        if (findSection.isMatchUpStation(section)) {
            addMiddleUpSection(section);
            return;
        }
        addFirstSection(section);
    }

    private void addDownSection(Section section) {
        Section findSection = getRegisteredDownStation(section);
        if (findSection.isMatchDownStation(section)) {
            addMiddleDownSection(section);
            return;
        }
        addLastSection(section);
    }

    private void addMiddleUpSection(Section section) {
        Section registeredUpSection = getRegisteredUpStation(section);
        validateDistance(registeredUpSection, section);

        Line line = section.getLine();
        int index = sections.indexOf(registeredUpSection);
        int newSectionDistance = registeredUpSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, registeredUpSection.getUpStation(), section.getDownStation(), section.getDistance()));
        sections.add(index + NEXT_VALUE, Section.of(line, section.getDownStation(), registeredUpSection.getDownStation(), newSectionDistance));
    }

    private void addMiddleDownSection(Section section) {
        Section registeredDownSection = getRegisteredDownStation(section);
        validateDistance(registeredDownSection, section);

        Line line = section.getLine();
        int index = sections.indexOf(registeredDownSection);
        int newSectionDistance = registeredDownSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, registeredDownSection.getUpStation(), section.getUpStation(), newSectionDistance));
        sections.add(index + NEXT_VALUE, Section.of(line, section.getUpStation(), registeredDownSection.getDownStation(), section.getDistance()));
    }

    private void addFirstSection(Section section) {
        sections.add(FIRST_INDEX, section);
    }

    private void addLastSection(Section section) {
        sections.add(section);
    }

    private void deleteEndStation(Station station) {
        if (isFirstStation(station)) {
            deleteFirstStation();
            return;
        }

        deleteLastStation();
    }

    private void deleteFirstStation() {
        sections.remove(FIRST_INDEX);
    }

    private void deleteLastStation() {
        int lastIndex = sections.size() - NEXT_VALUE;
        sections.remove(lastIndex);
    }

    private void deleteMiddleStation(Station station) {
        Section section = getSectionIncludeDownStation(station);
        Line line = section.getLine();

        int index = sections.indexOf(section);
        Section removeSection = sections.get(index + NEXT_VALUE);
        int distance = section.getDistance() + removeSection.getDistance();

        Section newSection = Section.of(line, section.getUpStation(), removeSection.getDownStation(), distance);
        sections.set(index, newSection);
        sections.remove(index + NEXT_VALUE);
    }

    private void validateDistance(Section oldSection, Section newSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidDistanceException(newSection.getDistance());
        }
    }

    private void validateAddSection(Section section) {
        validateAlreadyRegisteredSection(section);
        validateConnectStation(section);
    }

    private void validateAlreadyRegisteredSection(Section section) {
        List<Station> stations = getAllStations();
        if (stations.contains(section.getUpStation())
                && stations.contains(section.getDownStation())) {
            throw new AlreadyRegisteredStationException();
        }
    }

    private void validateConnectStation(Section section) {
        List<Station> stations = getAllStations();
        if (!stations.contains(section.getUpStation())
                && !stations.contains(section.getDownStation())) {
            throw new NotFoundConnectStationException();
        }
    }

    private void validateMinimumSection() {
        if (sections.size() <= MINIMUM_SIZE_SECTION) {
            throw new MinimumSectionException();
        }
    }

    private boolean isRegisteredUpStation(Section section) {
        return sections.stream()
                .anyMatch(it -> section.isContainStation(it.getUpStation()));
    }

    private boolean isEndStation(Station station) {
        return isFirstStation(station) || isLastStation(station);
    }

    private boolean isFirstStation(Station station) {
        Station firstUpStation = getFirstUpStation();
        return Objects.equals(firstUpStation, station);
    }

    private boolean isLastStation(Station station) {
        Station lastDownStation = getLastDownStation();
        return Objects.equals(lastDownStation, station);
    }

    private Section getRegisteredUpStation(Section section) {
        return sections.stream()
                .filter(it -> section.isContainStation(it.getUpStation()))
                .findFirst()
                .orElseThrow(NotFoundConnectStationException::new);
    }

    private Section getRegisteredDownStation(Section section) {
        return sections.stream()
                .filter(it -> section.isContainStation(it.getDownStation()))
                .findFirst()
                .orElseThrow(NotFoundConnectStationException::new);
    }

    private Section getSectionIncludeDownStation(Station station) {
        return sections.stream()
                .filter(it -> Objects.equals(it.getDownStation(), station))
                .findFirst()
                .orElseThrow(NotFoundConnectStationException::new);
    }

    private Station getFirstUpStation() {
        Section section = sections.get(FIRST_INDEX);
        return section.getUpStation();
    }

    private Station getLastDownStation() {
        int lastIndex = sections.size() - NEXT_VALUE;
        Section section = sections.get(lastIndex);
        return section.getDownStation();
    }

    private boolean isEmpty() {
        return sections.isEmpty();
    }

}
