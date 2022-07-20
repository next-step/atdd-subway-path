package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

@Getter
@Entity
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void updateNameAndColor(String updateName, String updateColor) {
        this.name = requireNonNullElseGet(updateName, () -> this.name);
        this.color = requireNonNullElseGet(updateColor, () -> this.color);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public void deleteStation(Station station) {
        this.sections.deleteStation(station);
    }
}
