package nextstep.subway.domain;

import nextstep.subway.exception.section.AlreadyRegisteredStationException;
import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.MinimumSectionException;
import nextstep.subway.exception.section.NotFoundConnectStationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
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
    @OrderColumn(name = "POSITION")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (isEmpty()) {
            sections.add(section);
            return;
        }

        validateAddSection(section);

        if (isAddableEndSection(section)) {
            addEndSection(section);
            return;
        }

        addMiddleSection(section);
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
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getLastDownStation());

        return stations;
    }

    private void addEndSection(Section section) {
        if (isAddableFirstSection(section)) {
            addFirstSection(section);
            return;
        }

        addLastSection(section);
    }

    private void addMiddleSection(Section section) {
        if (isAddableMiddleUpSection(section)) {
            addMiddleUpSection(section);
            return;
        }

        addMiddleDownSection(section);
    }

    private void addMiddleUpSection(Section section) {
        Section registeredSection = getRegisteredSection(section);
        validateDistance(registeredSection, section);

        Line line = section.getLine();
        int index = sections.indexOf(registeredSection);
        int newSectionDistance = registeredSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, registeredSection.getUpStation(), section.getUpStation(), newSectionDistance));
        sections.add(index + NEXT_VALUE, Section.of(line, section));
    }

    private void addMiddleDownSection(Section section) {
        Section registeredDownSection = getRegisteredSection(section);
        validateDistance(registeredDownSection, section);

        Line line = section.getLine();
        int index = sections.indexOf(registeredDownSection);
        int newSectionDistance = registeredDownSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, section));
        sections.add(index + NEXT_VALUE, Section.of(line, section.getDownStation(), registeredDownSection.getDownStation(), newSectionDistance));
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

    private boolean isAddableMiddleUpSection(Section section) {
        Station downStation = section.getDownStation();
        return getAllStations().stream()
                .anyMatch(it -> Objects.equals(it, downStation));
    }

    private boolean isAddableEndSection(Section section) {
        return isAddableFirstSection(section)
                || isAddableLastSection(section);
    }

    private boolean isAddableFirstSection(Section section) {
        Station downStation = section.getDownStation();
        return Objects.equals(getFirstUpStation(), downStation);
    }

    private boolean isAddableLastSection(Section section) {
        Station upStation = section.getUpStation();
        return Objects.equals(getLastDownStation(), upStation);
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

    private Section getRegisteredSection(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        return sections.stream()
                .filter(it -> it.isContainStation(upStation) || it.isContainStation(downStation))
                .findAny()
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
