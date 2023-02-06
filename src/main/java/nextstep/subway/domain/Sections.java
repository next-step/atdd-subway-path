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
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (!equalsLastStation(section.getUpStation())) {
            throw new IllegalArgumentException("구간을 추가할 수 없습니다.");
        }
        if (contains(section.getDownStation())) {
            throw new IllegalArgumentException("하행역이 이미 노선에 포함되어 있습니다.");
        }

        this.sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        if(sections.isEmpty()){
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        stations.addAll(sections.stream().map(Section::getDownStation)
                .collect(Collectors.toList()));


        return Collections.unmodifiableList(stations);
    }

    protected boolean equalsLastStation(Station station) {
        return sections.get(sections.size()-1).equalDownStation(station);
    }

    public void remove(Station station) {
        sections.remove(getStations().indexOf(station)-1);
    }

    public int size(){
        return sections.size();
    }

    public boolean contains(Station station){
        return getStations().contains(station);
    }
}
