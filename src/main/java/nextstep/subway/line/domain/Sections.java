package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateToAddSectionStationsAlreadyAdded(section);
        validateToAddSectionStationNone(section);

        if (addSectionInTheMiddle(section)) {
            return;
        }

        if (addSectionToUpStation(section)) {
            return;
        }

        if (addSectionToDownStation(section)) {
            return;
        }
    }

    public List<Section> getAll() {
        return sections;
    }

    public List<Station> getStations() {
        if (getAll().isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getAll().stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation() {
        Station downStation = getAll().get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getAll().stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }


    private boolean addSectionInTheMiddle(Section section) {
        if (addSectionInTheMiddleToUpStation(section)) {
            return true;
        }

        return addSectionInTheMiddleToDownStation(section);
    }

    private boolean addSectionInTheMiddleToUpStation(Section section) {
        Section upStationMatchedSection = sections.stream()
                .filter(it -> it.getUpStation() == section.getUpStation())
                .findFirst()
                .orElse(null);

        if (upStationMatchedSection != null) {
            validateToAddSectionDistance(upStationMatchedSection, section);

            int position = sections.indexOf(upStationMatchedSection);
            Section afterSection = new Section(
                    section.getLine(),
                    section.getDownStation(),
                    upStationMatchedSection.getDownStation(),
                    upStationMatchedSection.getDistance() - section.getDistance()
            );

            sections.remove(position);
            sections.add(position, section);
            sections.add(position + 1, afterSection);

            return true;
        }

        return false;
    }

    private boolean addSectionInTheMiddleToDownStation(Section section) {
        Section downStationMatchedSection = sections.stream()
                .filter(it -> it.getDownStation() == section.getDownStation())
                .findFirst()
                .orElse(null);

        if (downStationMatchedSection != null) {
            validateToAddSectionDistance(downStationMatchedSection, section);

            int position = sections.indexOf(downStationMatchedSection);
            Section beforeSection = new Section(
                    section.getLine(),
                    downStationMatchedSection.getUpStation(),
                    section.getUpStation(),
                    downStationMatchedSection.getDistance() - section.getDistance()
            );

            sections.remove(position);
            sections.add(position, beforeSection);
            sections.add(position + 1, section);

            return true;
        }

        return false;
    }

    private boolean addSectionToUpStation(Section section) {
        Section firstSection = sections.get(0);

        if (firstSection.getUpStation() == section.getDownStation()) {
            sections.add(0, section);
            return true;
        }

        return false;
    }

    private boolean addSectionToDownStation(Section section) {
        Section lastSection = sections.get(sections.size() - 1);

        if (lastSection.getDownStation() == section.getUpStation()) {
            sections.add(section);
            return true;
        }

        return false;
    }

    private void validateToAddSectionDistance(Section section, Section toAddSection) {
        if (toAddSection.getDistance() >= section.getDistance()) {
            throw new RuntimeException();
        }
    }

    private void validateToAddSectionStationsAlreadyAdded(Section toAddSection) {
        if (getStations().containsAll(toAddSection.getStations())) {
            throw new RuntimeException();
        }
    }

    private void validateToAddSectionStationNone(Section toAddSection) {
        if (!getStations().contains(toAddSection.getUpStation()) &&
                !getStations().contains(toAddSection.getDownStation())) {

            throw new RuntimeException();
        }
    }

    public void remove(Station station) {
        validateToRemove(station);

        if (removeUpStation(station)) {
            return;
        }

        if (removeDownStation(station)) {
            return;
        }

        if (removeMiddleStation(station)) {
            return;
        }
    }

    private void validateToRemove(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException();
        }

        getStations().stream()
                .filter(it -> it == station)
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

    private boolean removeUpStation(Station station) {
        if (getFirstStation() == station) {
            sections.remove(0);
            return true;
        }

        return false;
    }

    private Station getFirstStation() {
        List<Station> stations = getStations();
        return stations.get(0);
    }

    private boolean removeDownStation(Station station) {
        if (getLastStation() == station) {
            sections.remove(sections.size() - 1);
            return true;
        }

        return false;
    }

    private Station getLastStation() {
        List<Station> stations = getStations();
        return stations.get(stations.size() - 1);
    }

    private boolean removeMiddleStation(Station station) {
        Section upSection = sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst()
                .orElse(null);

        Section downSection = sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .orElse(null);

        if (upSection == null && downSection == null) {
            return false;
        }

        int position = sections.indexOf(upSection);
        Section mergedSection = new Section(
                upSection.getLine(),
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance()
        );

        sections.add(position, mergedSection);
        sections.remove(upSection);
        sections.remove(downSection);

        return true;
    }
}
