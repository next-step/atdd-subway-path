package nextstep.subway.domain;

import nextstep.subway.exception.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    private static final int MIN_SIZE = 1;

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
            validateSectionDistance(section);
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

    private boolean addableInMiddle(Section section) {
        return sections.stream()
                .map(s -> s.getUpStation())
                .anyMatch(upStation -> upStation.equals(section.getUpStation()));
    }

    public void removeSection(Line line, Station station) {
        validateNotOneSection();
        validateContainStation(station);

        if (station.equals(getDownStation())) {
            sections.remove(getLastSection());
            return;
        }
        if (station.equals(getUpStation())) {
            sections.remove(getFirstSection());
            return;
        }

        removeMiddleStation(line, station);
    }

    private void removeMiddleStation(Line line, Station station) {
        Section firstSection = sections.stream()
                .filter(s -> s.getDownStation().equals(station))
                .findFirst().orElseThrow(StationNotFoundException::new);
        Section secondSection = sections.stream()
                .filter(s -> s.getUpStation().equals(station))
                .findFirst().orElseThrow(StationNotFoundException::new);
        Section newSection = new Section(
                line,
                firstSection.getUpStation(),
                secondSection.getDownStation(),
                firstSection.getDistance() + secondSection.getDistance());
        sections.remove(firstSection);
        sections.remove(secondSection);
        sections.add(newSection);
    }

    private void removeIfTerminalStation(Station station) {
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
        if (stations.isEmpty()) return;
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new SectionContainsAnyStationException();
        }
    }

    private void validateSectionDistance(Section section) {
        if (sections.stream()
                .anyMatch(s ->s.getDistance() <= section.getDistance())) {
            throw new InvalidSectionDistanceException();
        }
    }

    private void validateNotOneSection() {
        if (sections.size() == MIN_SIZE) {
            throw new IllegalSectionRemoveException("구간이 하나인 노선에 삭제 요청을 할 수 없습니다.");
        }
    }

    private void validateContainStation(Station station) {
        List<Station> stations = getStations();
        if (!stations.contains(station)) {
            throw new IllegalSectionRemoveException("해당 노선에 요청하신 역이 존재하지 않습니다.");
        }
    }
}
