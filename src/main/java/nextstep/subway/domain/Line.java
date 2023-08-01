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
    //TODO: 이 Getter가 다시 돌아 왔는데... Path에서 Lines 에 대한 Sections을 구할 때
    //      어떻게 진행해야 잘하는 것인지 조금 궁금하네요..
    @Getter
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

    public Section getLastSection() {
        return sections.getLastSection();
    }
    public List<Section> getSections() {
        return sections.getSections();
    }

}
