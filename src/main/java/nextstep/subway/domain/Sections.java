package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section){
        this.sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        if(sections.isEmpty()){
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream().map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(sections.get(0).getUpStation());

        return Collections.unmodifiableList(stations);
    }

    public boolean equalsLastStation(Station station) {
        return sections.get(sections.size()-1).equalDownStation(station);
    }

    public void removeLast() {
        sections.remove(sections.size()-1);
    }
}
