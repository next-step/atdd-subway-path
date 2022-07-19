package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void changeName(String name) {
        if (isNullOrBlank(name)) {
            return;
        }
        this.name = name;
    }

    public void changeColor(String color) {
        if (isNullOrBlank(color)) {
            return;
        }
        this.color = color;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
    }

    public void deleteSection(Station station) {
        sections.delete(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }
}
