package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

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

    public boolean hasSections() {
        return sections != null && sections.getSectionsCount() > 0;
    }

    public void addSection(Section section) {
        if (!hasSections()) {
            sections = new Sections(section);
            return;
        }

        sections.addSection(section);
    }

    public int getSectionsCount() {
        if (!hasSections()) {
            return 0;
        }

        return sections.getSectionsCount();
    }

    public Section getFirstSection() {
        if (!hasSections()) {
            throw new NoSuchElementException();
        }

        return sections.getFirstSection();
    }

    public List<Station> getStations() {
        if (!hasSections()) {
            return new ArrayList<>();
        }

        return sections.getStations();
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public int getLength() {
        if (!hasSections()) {
            return 0;
        }

        return sections.getTotalDistance();
    }

}
