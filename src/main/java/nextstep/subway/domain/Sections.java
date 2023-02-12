package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

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

    public boolean exist(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStationEquals(station)
                        || section.isDownStationEquals(station));
    }

    public List<Section> getSection() {
        return sections;
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        Station station = getFinalUpStation();
        stations.add(station);
        while (existNextSection(station)) {
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    private boolean existNextSection(Station station) {
        return this.sections.stream()
                .filter(Section::isExistUpStation)
                .anyMatch(section -> section.isUpStationEquals(station));
    }

    private Section findNextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isUpStationEquals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    private Station getFinalUpStation() {
        Station finalUpStation = this.sections.get(0).getUpStation();
        while (existPreSection(finalUpStation)) {
            Section preSection = findPreSection(finalUpStation);
            finalUpStation = preSection.getUpStation();
        }
        return finalUpStation;
    }

    private boolean existPreSection(Station station) {
        return this.sections.stream()
                .filter(Section::isExistDownStation)
                .anyMatch(section -> section.isDownStationEquals(station));
    }

    private Section findPreSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isDownStationEquals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 존재하지 않습니다."));
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
