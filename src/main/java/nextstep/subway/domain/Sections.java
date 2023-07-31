package nextstep.subway.domain;

import lombok.NoArgsConstructor;
import nextstep.subway.domain.delete.SectionDeleteStrategy;
import nextstep.subway.domain.delete.SectionDeleteType;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addFirst(Section section) {
        sections.add(0, section);
    }

    public void addLast(Section section) {
        sections.add(section);
    }

    public void addMiddleUpStation(Section section) {
        Section existSection = sections.stream()
                .filter(e -> e.equalUpStation(section.getUpStation()))
                .findAny()
                .orElse(null);
        int index = sections.indexOf(existSection);
        sections.get(index).updateDownStation(section);
        sections.add(index, section);
    }

    public void addMiddleDownStation(Section section) {
        Section existSection = sections.stream()
                .filter(e -> e.equalDownStation(section.getDownStation()))
                .findAny()
                .orElse(null);
        int index = sections.indexOf(existSection);
        sections.get(index).updateUpStation(section);
        sections.add(index + 1, section);
    }

    private boolean containsAtUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    private boolean containsAtDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(downStation));
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());

        return stations;
    }

    public SectionAddType findAddType(Station upStation, Station downStation) {
        Stations stations = new Stations(getStations());
        if (!stations.empty() && stations.doesNotContainAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (stations.containsAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }

        if (stations.empty() || stations.equalLastStation(upStation)) {
            return SectionAddType.LAST;
        }
        if (stations.equalFirstStation(downStation)) {
            return SectionAddType.FIRST;
        }
        if (containsAtUpStation(upStation)) {
            return SectionAddType.MIDDLE_UP_STATION;
        }
        if (containsAtDownStation(downStation)) {
            return SectionAddType.MIDDLE_DOWN_STATION;
        }
        throw new IllegalArgumentException();
    }

    public boolean remainOneSection() {
        return sections.size() == 1;
    }

    public void remove(Station station) {
        Stations stations = new Stations(getStations());
        SectionDeleteStrategy strategy = SectionDeleteType.find(stations, station);
        strategy.delete(this, station);
    }

    public void remove(Section lastSection) {
        sections.remove(lastSection);
    }

    public Section findFirst() {
        return sections.get(0);
    }

    public Section findLast() {
        return sections.get(sections.size() - 1);
    }

    public List<Section> findIncluded(Station station) {
        return sections.stream()
                .filter(section -> section.has(station))
                .collect(Collectors.toList());
    }
}
