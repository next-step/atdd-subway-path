package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    private static final int MIN_SIZE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateLine(String name, String color){
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if(sections.isEmpty()){
            sections.add(section);
            section.setLine(this);
            return;
        }

        Section lastSection = sections.get(sections.size() - 1);
        Station endStation = lastSection.getDownStation();
        if(!endStation.equals(section.getUpStation())){
            throw new IllegalArgumentException("요청한 상행역이 하행종점역과 연결되지 않습니다.");
        }

        sections.add(section);
        section.setLine(this);
    }


    public void removeStation(Station newStation) {
        if(sections.size() == MIN_SIZE){
            throw new IllegalArgumentException("구간이 1개일 경우 삭제가 불가능합니다.");
        }
        List<Station> stations = getStations();
        if(!stations.get(stations.size()-1).equals(newStation)){
            throw new IllegalArgumentException("마지막 역만 삭제가 가능합니다.");
        }
        sections.remove(sections.size()-1);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sections.get(sections.size()-1).getDownStation());
        return stations;
    }
}
