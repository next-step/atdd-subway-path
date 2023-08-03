package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {
    // TODO: 짜릿하네요... SectionService를 도입해서 Line에 대한 객체의 노출을 줄였습니다.
    //
    @Embedded
    private final Sections sections = new Sections();
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Column(length = 25, nullable = false)
    private String name;
    @Getter
    @Column(length = 25, nullable = false)
    private String color;

    @Builder
    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Station upStation, Station downStation, Long distance) {
        Line line = new Line(name, color);
        Section section = Section.of(line, distance, upStation, downStation);
        line.addSection(section);
        return line;
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Section section) {
        sections.addSection(section, this);
    }

    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

}
