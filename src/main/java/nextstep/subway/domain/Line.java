package nextstep.subway.domain;

import nextstep.subway.util.LineSectionChecker;
import nextstep.subway.util.LineSectionMaker;
import nextstep.subway.util.SectionUpdatePosition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public void addSection(Section section) {
        //기존 노선의 구간 크기 확인
        int lineSize = section.getLine().getSections().size();

        //지하철 노선 초기 생성 시
        if(lineSize == 0){
            sections.add(section);
        }
        //기존 노선에서 추가 작업 시
        if(lineSize  > 0){
            LineSectionChecker checker = new LineSectionChecker(sections, section);
            LineSectionMaker maker = new LineSectionMaker();
            sections = maker.makeSection(checker.checkAddPosition(), sections, section);
        }

    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void removeSection(int lastSection) {
        this.sections.remove(lastSection);
    }
}
