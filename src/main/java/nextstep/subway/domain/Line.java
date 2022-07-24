package nextstep.subway.domain;

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

    public Section getUpStation() {
        return sections.stream()
                .filter(sec1 -> sections.stream()
                        .noneMatch(sec2 -> sec1.getUpStation().equals(sec2.getDownStation())))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("WRONG_SECTION_IMFORMATION"));

    }

    public Section getDownStation() {
        return sections.stream()
                .filter(sec1 -> sections.stream()
                        .noneMatch(sec2 -> sec1.getDownStation().equals(sec2.getUpStation())))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("WRONG_SECTION_IMFORMATION"));
    }

    public List<Section> getSortedSections(List<Section> sections, Section firstSection) {
        List<Section> results = new ArrayList<>();
        results.add(firstSection);

        while (results.size() < sections.size()) {
            Section nextSection = sections.stream()
                    .filter(sec -> sec.getUpStation().getId().equals(results.get(results.size() - 1).getDownStation().getId()))
                    .findAny().orElseThrow();
            results.add(nextSection);
        }


        return results;
    }

    public void addSection(Section section) {
        //기존 노선의 구간 크기 확인
        int sectionSize = this.sections.size();

        //지하철 노선 초기 생성 시
        if (sectionSize == 0) {
            sections.add(section);
        }
        //기존 노선에서 추가 작업 시
        if (sectionSize > 0) {
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
