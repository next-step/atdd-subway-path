package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nextstep.subway.dto.LineRequest;

@Entity
@Table(name = "line")
@Builder
@Getter
@AllArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;

    @Override
    public String toString() {
        return "Line{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", color='" + color + '\'' +
               ", sections=" + sections +
               '}';
    }

    @Embedded
    private Sections sections = new Sections();

    public Line() {

    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }
    public static Line of(LineRequest lineRequest, Station upStation, Station downStation) {
        Line line = Line.builder()
                        .name(lineRequest.getName())
                        .color(lineRequest.getColor())
                        .sections(new Sections())
                        .build();
        line.addSection(new Section(upStation, downStation, lineRequest.getDistance()));
        return line;
    }

    public void updateLine(String name, String color) {
        if (Objects.isNull(name) || Objects.isNull(color)) {
            throw new IllegalArgumentException("값이 존재하지 않습니다.");
        }
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteSection(Station station) {
        sections.removeSection(station);
    }

    public Stations getStations() {
        return sections.getStations();
    }
}
