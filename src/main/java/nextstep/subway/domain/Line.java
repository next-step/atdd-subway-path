package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.exception.InvalidLineColorException;
import nextstep.subway.domain.exception.InvalidLineNameException;
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
        if (!StringUtils.hasText(name)) {
            throw new InvalidLineNameException();
        }
        this.name = name;
    }

    public void changeColor(String color) {
        if (!StringUtils.hasText(color)) {
            throw new InvalidLineColorException();
        }
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        sections.add(section);
        section.makeRelation(this);
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
