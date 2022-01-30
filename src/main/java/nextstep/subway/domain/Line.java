package nextstep.subway.domain;

import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {

        if (sections.isEmpty()) {
            sections.add(new Section(this, upStation, downStation, distance));

            return;
        }
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(final String name, final String color) {
        if (!ObjectUtils.isEmpty(name)) {
            this.name = name;
        }
        if (!ObjectUtils.isEmpty(color)) {
            this.color = color;
        }
    }

    public void deleteSection(final Station station) {
        if (!this.sections.get(this.sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(this.sections.size() - 1);
    }

    public List<Section> getSections() {

        return sections;
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
}
