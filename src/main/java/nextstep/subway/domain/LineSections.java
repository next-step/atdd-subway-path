package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getAllSections() {
        return sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (this.sections.isEmpty()) {
            extendSection(line ,upStation, downStation, distance);
            return;
        }

        checkStationStatus(upStation, downStation);

        findSameUpStation(upStation)
            .ifPresentOrElse(
                oldSection -> splitSection(line, downStation, distance, oldSection),
                () -> extendSection(line, upStation, downStation, distance)
            );
    }

    private Optional<Section> findSameUpStation(Station upStation) {
        return sections.stream().filter(s -> s.getUpStation().equals(upStation))
            .findFirst();
    }

    private void splitSection(Line line, Station downStation, int distance, Section oldSection) {
        Station oldUpstation = oldSection.getUpStation();
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkDistance(distance, oldDistance);

        this.sections.remove(oldSection);
        this.sections.addAll(List.of(
            Section.builder()
                .line(line)
                .upStation(oldUpstation)
                .downStation(downStation)
                .distance(distance)
                .build(),
            Section.builder()
                .line(line)
                .upStation(downStation)
                .downStation(oldDownStation)
                .distance(oldDistance - distance)
                .build()
        ));
    }

    private void checkStationStatus(Station upStation, Station downStation) {
        List<Station> allStations = getAllStations();
        if (stationsAlreadyExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException(LineErrorMessage.ADD_SECTION_STATIONS_ALREADY_EXIST.getMessage());
        }
        if (stationsNotExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException(LineErrorMessage.ADD_SECTION_STATIONS_NOT_EXIST.getMessage());
        }
    }

    private boolean stationsNotExist(Station upStation, Station downStation, List<Station> allStations) {
        return !allStations.contains(upStation) && !allStations.contains(downStation);
    }

    private boolean stationsAlreadyExist(Station upStation, Station downStation, List<Station> allStations) {
        return allStations.contains(upStation) && allStations.contains(downStation);
    }

    private void checkDistance(int distance, int oldDistance) {
        if (distance >= oldDistance) throw new IllegalArgumentException(LineErrorMessage.ADD_SECTION_INVALID_DISTANCE.getMessage());
    }

    private void extendSection(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        if (this.sections.isEmpty()) {
            return stations;
        }

        stations.add(this.sections.get(0).getUpStation());
        for (Section section : this.sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> stationMap = this.sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        List<Station> orderedStations = new ArrayList<>();

        Section endStations = findBothEndsStations();
        Station upStation = endStations.getUpStation();
        Station downStation;

        orderedStations.add(upStation);

        while (upStation != endStations.getDownStation()) {
            downStation = stationMap.getOrDefault(upStation, null);
            orderedStations.add(downStation);
            upStation = downStation;
        }

        return orderedStations;
    }

    private Section findBothEndsStations() {
        Set<Station> upStations = this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
        Set<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());

        HashSet<Station> allMiddleStations = getAllMiddleStations(upStations, downStations);

        upStations.removeAll(allMiddleStations);
        downStations.removeAll(allMiddleStations);

        return Section.builder()
            .upStation(upStations.stream().findFirst().orElse(null))
            .downStation(downStations.stream().findFirst().orElse(null))
            .build();
    }

    private static HashSet<Station> getAllMiddleStations(Set<Station> upStations, Set<Station> downStations) {
        HashSet<Station> intersection = new HashSet<>(upStations);
        intersection.retainAll(downStations);
        return intersection;
    }

    public void removeSection(Line line, Station station) {
        Station targetStation = findStation(station);

        checkSectionsSize();

        List<Section> targetSections = findSectionsByStation(targetStation);

        if (targetSections.size() > 1) {
            Section firstSection = targetSections.get(0);
            Section secondSection = targetSections.get(1);
            extendSection(line, firstSection.getUpStation(), secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance());
            this.sections.removeIf(section -> section.equals(targetSections.get(0)) || section.equals(targetSections.get(1)));
            return;
        }

        this.sections.removeIf(section -> section.equals(targetSections.get(0)));
    }

    private List<Section> findSectionsByStation(Station targetStation) {
        return this.sections.stream()
            .filter(section -> section.getUpStation().equals(targetStation) || section.getDownStation().equals(targetStation))
            .collect(Collectors.toList());
    }

    private void checkSectionsSize() {
        if (sections.size() < 2) {
            throw new IllegalArgumentException(LineErrorMessage.REMOVE_SECTION_LAST_ONE.getMessage());
        }
    }

    private Station findStation(Station station) {
        return getAllStations().stream()
            .filter(station::equals)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(LineErrorMessage.REMOVE_SECTION_STATIONS_NOT_EXISTS.getMessage()));
    }
}
