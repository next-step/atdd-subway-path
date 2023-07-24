package nextstep.subway.line.repository;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public
class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    @Builder
    Line(String name, String color, Section initSection) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(List.of(initSection));
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }
}
