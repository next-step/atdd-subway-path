package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineSection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (this.sections.isEmpty()) {
            extendSection(line ,upStation, downStation, distance);
            return;
        }

        checkStationStatus(upStation, downStation);

        Section oldSection = findSameUpStation(upStation);
        if (oldSection != null) {
            splitSection(line, downStation, distance, oldSection);
            return;
        }

        extendSection(line, upStation, downStation, distance);
    }

    private Section findSameUpStation(Station upStation) {
        return this.sections.stream().filter(s -> s.getUpStation().equals(upStation))
            .findFirst()
            .orElse(null);
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
            throw new IllegalArgumentException(LineErrorMessage.STATIONS_ALREADY_EXIST.getMessage());
        }
        if (stationsNotExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException(LineErrorMessage.STATIONS_NOT_EXIST.getMessage());
        }
    }

    private List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        Section anySection = this.sections.stream().findFirst().orElse(null);

        if (anySection == null) {
            return stations;
        }

        stations.add(anySection.getUpStation());
        for (Section section : this.sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    private boolean stationsNotExist(Station upStation, Station downStation, List<Station> allStations) {
        return !allStations.contains(upStation) && !allStations.contains(downStation);
    }

    private boolean stationsAlreadyExist(Station upStation, Station downStation, List<Station> allStations) {
        return allStations.contains(upStation) && allStations.contains(downStation);
    }

    private void checkDistance(int distance, int oldDistance) {
        if (distance >= oldDistance) throw new IllegalArgumentException(LineErrorMessage.INVALID_DISTANCE.getMessage());
    }

    private void extendSection(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }

    public void removeSection(Station downEndStation) {
        if (!this.sections.get(this.sections.size() - 1).getDownStation().equals(downEndStation)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }
}
