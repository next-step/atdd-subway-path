package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public Line(Long id, String name, String color) {
        this.id = id;
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

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section){
        Section adjacentSection = findAdjacentSection(section);
        if(adjacentSection.isInMiddle(section)) adjacentSection.changeUpStationAndDistance(section);
        this.getSections().add(section);
    }

    private Section findAdjacentSection(Section section){
        List<Section> adjacentSections = this.getSections().stream().filter(o -> o.isContainStation(section.getUpStation(), section.getDownStation())).collect(Collectors.toList());
        if(adjacentSections.isEmpty()) throw new IllegalArgumentException("지하철 노선에 구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다");
        return adjacentSections.get(0);
    }

    public void removeSection(Section section){
        sections.remove(section);
    }
}
