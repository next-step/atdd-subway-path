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

        validateAlreadyRegisteredSection(section);
        validateConnectStation(section);

        if (isRegisteredUpStation(section.getUpStation())) {
            addMiddleSection(section);
            return;
        }

        if (isRegisteredUpStation(section.getDownStation())) {
            addFirstSection(section);
            return;
        }

        if (isRegisteredDownStation(section.getUpStation())) {
            addLastSection(section);
            return;
        }
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

    private void addFirstSection(Section section) {
        sections.add(0, section);
    }

    private void addMiddleSection(Section section) {
        Section upSection = findUpSection(section);
        int originDistance = upSection.getDistance();
        int newSectionDistance = section.getDistance();
        validateDistance(upSection, section);

        int addIndex = sections.indexOf(upSection) + 1;
        Section newSection = Section.of(section.getLine(),
                section.getDownStation(),
                upSection.getDownStation(),
                originDistance - newSectionDistance);
        sections.add(addIndex, newSection);

        upSection.updateDownStation(section.getDownStation(), newSectionDistance);
    }

    private void addLastSection(Section section) {
        sections.add(section);
    }

    private Section findUpSection(Section section) {
        Station upStation = section.getUpStation();

        return sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findAny()
                .orElseThrow(() -> new RuntimeException(""));
    }

    private void validateDistance(Section oldSection, Section newSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidDistanceException(newSection.getDistance());
        }
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

    private boolean isRegisteredUpStation(Station station) {
        return sections.stream()
                .anyMatch(it -> it.getUpStation().equals(station));
    }

    private boolean isRegisteredDownStation(Station station) {
        return sections.stream()
                .anyMatch(it -> it.getDownStation().equals(station));
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
