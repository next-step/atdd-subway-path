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

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, getFirstStation());

        return stations;
    }

    private Station getFirstStation() {
        return sections.get(0)
                .getUpStation();
    }
}
