package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.exception.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if (sections.isEmpty()) {
            return stations;
        }
        Station prev = getUpStation();
        while(findNextStationOf(prev).isPresent()) {
            stations.add(prev);
            prev = findNextStationOf(prev).get();
        }
        stations.add(prev);

        return stations;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        validateContainsNotAllStation(upStation, downStation);
        validateContainsAnyStation(upStation, downStation);
        if (addableInMiddle(section)) {
            Section originalSection = sections.stream()
                    .filter(s-> upStation.equals(s.getUpStation()))
                    .findFirst().get();
            Section newSection = new Section(
                    line, downStation, getDownStation(),
                    originalSection.getDistance()- distance);
            sections.remove(originalSection);
            sections.add(newSection);
        }
        sections.add(section);
    }

    public void removeSection() {
        sections.remove(sections.size()-1);
    }

    public Station getUpStation() {
        return sections.stream()
                .map(section -> section.getUpStation())
                .filter(upStation -> sections.stream()
                        .noneMatch(section -> upStation.equals(section.getDownStation())))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    public Station getDownStation() {
        return sections.stream()
                .map(section -> section.getDownStation())
                .filter(downStation -> sections.stream()
                        .noneMatch(section -> downStation.equals(section.getUpStation())))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Optional<Station> findNextStationOf(Station prev) {
        return sections.stream()
                .filter(section -> prev.equals(section.getUpStation()))
                .map(section -> section.getDownStation())
                .findFirst();
    }

    public Section getFirstSection() {
        return sections.stream()
                .filter(section -> getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(SectionNotFoundException::new);
    }

    public Section getLastSection() {
        return sections.stream()
                .filter(section -> getDownStation().equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(SectionNotFoundException::new);
    }

    private void validateContainsNotAllStation(Station upStation, Station downStation) {
        List<Station> stations = getStations();
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new SectionContainsAllStationException();
        }
    }

    private void validateContainsAnyStation(Station upStation, Station downStation) {
        List<Station> stations = getStations();
        if (stations.size() == 0) return;
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new SectionContainsAnyStationException();
        }
    }

    private boolean addableInMiddle(Section section) {
        return sections.stream()
                .filter(s -> s.getDistance() > section.getDistance())
                .map(s -> s.getUpStation())
                .anyMatch(upStation -> upStation.equals(section.getUpStation()));
    }
}
