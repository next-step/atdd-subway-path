package nextstep.subway.domain;

import nextstep.subway.exception.SectionBadRequestException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
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

    private Sections newSections = new Sections();

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
        return newSections.getSections();
    }

    public void addSection(Section section) {
        newSections.add(section);
    }

    public List<Station> getStations() {
        return newSections.getStations();
    }

    public void removeSection() {
        if (newSections.isEmpty()) {
            throw new SectionBadRequestException("구간이 존재하지 않습니다.");
        }
        if (newSections.hasEnoughSize()) {
            throw new SectionBadRequestException("현재 노선은 구간이 1개 입니다.");
        }

        /*
        * TODO
        * 마지막 구간의 하행역만 삭제 할 수 있는 조건 작성하기
        * */

        int end = sections.size() - 1;
        sections.remove(end);
    }
}
