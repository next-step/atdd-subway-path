package nextstep.subway.line;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.exception.HttpBadRequestException;
import nextstep.subway.line.section.Section;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;


    @JsonManagedReference
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section section) {
        this.sections.add(section);
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

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void deleteSection(Section section) {
        if (sections.size() == 1) {
            throw new HttpBadRequestException("노선에는 최소 한 개의 구간이 존재해야 합니다.");
        }

        if(!Objects.equals(sections.get(sections.size()-1), section)){
            throw new HttpBadRequestException("노선의 마지막 구간만 삭제할 수 있습니다.");
        }

        sections.remove(section);
    }

    public void removeSection(Section section) {
        this.getSections().remove(section);
    }
}
