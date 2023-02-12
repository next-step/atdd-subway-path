package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addNew(Line line, Section section) {
        this.sections.add(new Section(line, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    public void addInUp(Section newSection) {
        this.sections.stream()
                .filter(section -> section.isUpStationEquals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    public void addInDown(Section newSection) {
        this.sections.stream()
                .filter(section -> section.isDownStationEquals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(newSection.getUpStation(), newSection.getDistance()));
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean isAddInUpSection(Section newSection) {
        return getStations().get().contains(newSection.getUpStation());
    }

    public boolean isAddInDownSection(Section newSection) {
        return getStations().get().contains(newSection.getDownStation());
    }

    public List<Section> get() {
        return sections;
    }

    public Stations getStations() {
        if (isEmpty()) {
            return Stations.of();
        }
        Stations stations = Stations.of(getFinalUpStation());
        Station finalDownStation = getFinalDownStation();
        while (!stations.isFinalDownStationEqualTo(finalDownStation)) {
            findNextStation(stations);
        }
        return stations;
    }

    private void findNextStation(Stations stations) {
        this.sections.stream()
                .filter(section -> stations.isFinalDownStationEqualTo(section.getUpStation()))
                .findFirst()
                .map(Section::getDownStation)
                .ifPresent(stations::add);
    }

    private Station getFinalUpStation() {
        return findFinalStation(Section::getUpStation, getDownStations());
    }

    private Station getFinalDownStation() {
        return findFinalStation(Section::getDownStation, getUpStations());
    }

    private Station findFinalStation(Function<Section, Station> getStation, List<Station> stations) {
        return sections.stream()
                .map(getStation)
                .filter(station -> !stations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("종점역이 존재하지 않습니다."));
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public List<Integer> getSectionDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

    public void removeSection(Station station) {
        if (!getFinalDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

    public void removeSection(String stationName) {
        sections.stream()
                .filter(section -> section.isStationNameEqualTo(stationName))
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }
}
