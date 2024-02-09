package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Column(nullable = false)
    private int distance;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
        this.distance += section.getDistance();
    }

    public void deleteSection(Section section) {
        sections.deleteSection(section);
        this.distance -= section.getDistance();
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
