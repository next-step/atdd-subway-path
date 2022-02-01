package nextstep.subway.domain;

import nextstep.subway.exception.section.*;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

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

    public void deleteStation(Station deleteStation) {
        validateDeleteLastDownStation(deleteStation);
        validateMinimumSection();

        int lastIndex = sections.size() - 1;
        sections.remove(lastIndex);
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
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
        int nextIndex = index + 1;
        int newSectionDistance = registeredUpSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, registeredUpSection.getUpStation(), section.getDownStation(), section.getDistance()));
        sections.add(nextIndex, Section.of(line, section.getDownStation(), registeredUpSection.getDownStation(), newSectionDistance));
    }

    private void addMiddleDownSection(Section section) {
        Section registeredDownSection = getRegisteredDownStation(section);
        validateDistance(registeredDownSection, section);

        Line line = section.getLine();
        int index = sections.indexOf(registeredDownSection);
        int nextIndex = index + 1;
        int newSectionDistance = registeredDownSection.getDistance() - section.getDistance();

        sections.set(index, Section.of(line, registeredDownSection.getUpStation(), section.getUpStation(), newSectionDistance));
        sections.add(nextIndex, Section.of(line, section.getUpStation(), registeredDownSection.getDownStation(), section.getDistance()));
    }

    private void addFirstSection(Section section) {
        sections.add(FIRST_INDEX, section);
    }

    private void addLastSection(Section section) {
        sections.add(section);
    }

    private void validateDistance(Section oldSection, Section newSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidDistanceException(newSection.getDistance());
        }
    }

    public void validateAddSection(Section section) {
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

    private void validateDeleteLastDownStation(Station deleteStation) {
        if (isEmpty()) {
            return;
        }

        Station lastDownStation = getLastDownStation();
        if (!Objects.equals(lastDownStation, deleteStation)) {
            throw new DeleteLastDownStationException(deleteStation.getName());
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

    private Station getLastDownStation() {
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        return section.getDownStation();
    }

    private boolean isEmpty() {
        return sections.isEmpty();
    }

}
