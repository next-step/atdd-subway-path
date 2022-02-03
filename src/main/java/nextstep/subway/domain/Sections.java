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

    protected Sections() {
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section section = Section.of(line, upStation, downStation, distance);

        sections.add(section);
    }

    public Station getLastDownStation() {
        int lastIndex = sections.size() - 1;

        return sections.get(lastIndex).getDownStation();
    }

    public List<Station> getStations() {
        List<Station> stationList = sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());

        stationList.add(getLastDownStation());

        return stationList;
    }

    public boolean existStation(Station downStation) {
        return getStations().stream()
                .anyMatch(station -> station.equals(downStation));
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }
}
