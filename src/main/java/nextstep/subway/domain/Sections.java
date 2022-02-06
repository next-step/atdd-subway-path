package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        if (!sections.isEmpty()) {
            checkLastDownStation(section.getUpStation());
            checkExistStationInLine(section.getDownStation());
        }
        this.sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if(!sections.isEmpty()){
            stations.add(findFirstStation());
            stations.addAll(findDownStations());
        }

        return stations;
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
    }

    private Station getLastStation() {
        int size = sections.size();
        return sections.get(size - 1)
                .getDownStation();
    }

    private Station findFirstStation() {
        return sections.get(0)
                .getUpStation();
    }

    private void checkExistStationInLine(Station downStation) {
        boolean exist = this.sections.stream()
                .anyMatch(section -> section.getId() == downStation.getId());

        if (exist) {
            throw new IllegalArgumentException();
        }
    }

    private void checkLastDownStation(Station upStation) {
        if (!upStation.equals(getLastStation())) {
            throw new IllegalArgumentException();
        }
    }
}