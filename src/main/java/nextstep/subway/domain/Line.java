package nextstep.subway.domain;

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded private Sections sections = new Sections();

    protected Line() {}

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section newSection) {
        sections.addSection(newSection);
    }

    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

    public List<Station> getSortedStations() {
        return sections.getSortedStations();
    }


    public List<Section> getSections() {
        return sections.getSections();
    }

    public void update(@NotBlank String name, @NotBlank String color) {

        this.name = name;
        this.color = color;
    }
}