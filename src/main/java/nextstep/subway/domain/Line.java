package nextstep.subway.domain;

import nextstep.subway.domain.exception.SubwayException;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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
        return sections.stations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
    }

    public void removeSection(long stationId) {
        sections.remove(stationId);
    }

    public void update(String name, String color) {
        if (name == null || color == null) {
            throw new SubwayException("노선의 이름 또는 색상은 null 이 될 수 없습니다.");
        }

        this.name = name;
        this.color = color;
    }
}
