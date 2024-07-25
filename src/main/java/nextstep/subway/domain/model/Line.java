package nextstep.subway.domain.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.domain.service.SectionAdditionStrategy;

@Entity
public class Line {
    public static final String SECTION_NOT_FOUND_MESSAGE = "구간을 찾을 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = new LineName(name);
        this.color = new LineColor(color);
        this.sections = new Sections();
    }

    public Line(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        this.sections = new Sections();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }

    public Line getUpdated(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        return this;
    }

    public void addSection(Section section) {
        sections.addSection(this, section);
    }

    public void addSection(SectionAdditionStrategy sectionAdditionStrategy, Section section) {
        sections.addSection(sectionAdditionStrategy, this, section);
    }

    public void removeSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalStateException(SECTION_NOT_FOUND_MESSAGE);
        }

        sections.removeSection(station);
    }

    public Sections getSections() {
        return sections;
    }

    public List<Section> getUnmodifiableSections() {
        return sections.toUnmodifiableList();
    }

    public List<Section> getOrderedUnmodifiableSections() {
        return sections.getOrderedUnmodifiableSections();
    }

    public List<Station> getOrderedUnmodifiableStations() {
        return sections.getOrderedUnmodifiableStations();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Line line = (Line)object;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}