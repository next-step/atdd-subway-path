package nextstep.subway.domain;

import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<Section> sortSections() {
        Section upSection = sections.stream()
                .filter(section -> isUpStation(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상행역이 없습니다."));
        List<Section> sort = new ArrayList<>();
        sort.add(upSection);
        Optional<Section> nextSection = findNextSection(upSection.getDownStation());
        if(nextSection.isPresent()) {
            sort.add(nextSection.get());
            findNextSection(nextSection.get().getDownStation());
        }
        return sort;
    }

    // 상행역으로 등록되어 있는지 찾는 메소드
    private boolean isUpStation(Station station) {
        return sections.stream().map(Section::getUpStation).collect(Collectors.toList()).contains(station);
    }

    // 하행역으로 등록되어있는지 찾는 메소드
    private boolean isDownStation(Station station) {
        return sections.stream().map(Section::getDownStation).collect(Collectors.toList()).contains(station);
    }

    // 다음 역을 찾는 메소드
    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst();
    }
    public void addSection(Section section) {
        if(isUpStation(section.getUpStation())) {
            addMiddleUpSection(section);
            return;
        }
        if(isDownStation(section.getDownStation())) {
            addMiddleDownSection(section);
            return;
        }
        this.sections.add(section);
    }

    // 추가되는 구간의 상행선 역이 기존 구간의 상행선으로 등록되어있으면 구간 사이에 들어간다.
    private void addMiddleUpSection(Section newSection) {
        Section prevSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .get();
        prevSection.updateSection(newSection, true);
    }

    // 추가되는 구간이 하행선 역이 기존 구간의 하행선으로 등록되어있으면 구간 사이에 들어간다.
    private void addMiddleDownSection(Section newSection) {
        Section prevSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .get();
        prevSection.updateSection(newSection, false);
    }
}
