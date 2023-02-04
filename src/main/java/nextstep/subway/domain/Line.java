package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }

    public void addSections(Section section) {
        if(sections.isEmpty()){
            sections.addSection(section); return;
        }
        if(!sections.equalsLastStation(section.getUpStation())){
            throw new IllegalArgumentException("구간을 추가할 수 없습니다.");
        }
        if(sections.contains(section.getDownStation())){
            throw new IllegalArgumentException("하행역이 이미 노선에 포함되어 있습니다.");
        }

        sections.addSection(section);

    }

    public boolean isEmptySections() {
        return sections.isEmpty();
    }

    public List<Station> getAllStations() {
        return sections.getStations();
    }

    public boolean equalsLastStation(Station station) {
        return sections.equalsLastStation(station);
    }

    public void removeLastSection() {
        sections.removeLast();
    }
}
