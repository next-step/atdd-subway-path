package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private List<Station> getAllStations() {
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
        List<Station> upStations = this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        Station upEndStation = upStations.stream()
            .filter(s -> !downStations.contains(s))
            .findFirst()
            .orElse(null);
        Station downEndStation = downStations.stream()
            .filter(s -> !upStations.contains(s))
            .findFirst()
            .orElse(null);

        return Section.builder()
            .upStation(upEndStation)
            .downStation(downEndStation)
            .build();
    }

    public void removeSection(Line line, Station station) {
        Station targetStation = getAllStations().stream()
            .filter(station::equals)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(LineErrorMessage.REMOVE_SECTION_STATIONS_NOT_EXISTS.getMessage()));

        if (sections.size() < 2) {
            throw new IllegalArgumentException(LineErrorMessage.REMOVE_SECTION_LAST_ONE.getMessage());
        }

        List<Section> targetSections = this.sections.stream()
            .filter(section -> section.getUpStation().equals(targetStation) || section.getDownStation().equals(targetStation))
            .collect(Collectors.toList());

        if (targetSections.size() > 1) {
            Section firstSection = targetSections.get(0);
            Section secondSection = targetSections.get(1);
            extendSection(line, firstSection.getUpStation(), secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance());
            this.sections.removeIf(section -> section.equals(targetSections.get(0)) || section.equals(targetSections.get(1)));
            return;
        }

        this.sections.removeIf(section -> section.equals(targetSections.get(0)));
    }
}
