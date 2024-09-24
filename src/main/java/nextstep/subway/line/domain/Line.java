package nextstep.subway.line.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Sections;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }
}
