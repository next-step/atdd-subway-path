package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Line {

    private static final int MIN_SECTION_SIZE = 1;

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

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public Section getLastSection() {
        return this.sections.getLastSection();
    }
}