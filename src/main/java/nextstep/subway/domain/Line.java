package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void removeSection(final Station station) {
        sections.removeSection(station);
    }

    public void addSection(final Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.addSection(Section.builder()
                               .upStation(upStation)
                               .downStation(downStation)
                               .distance(distance).build());
    }

    public void update(final String name, final String color) {
        this.name = StringUtils.hasText(name) ? name : this.name;
        this.color = StringUtils.hasText(color) ? color : this.color;
    }
}
