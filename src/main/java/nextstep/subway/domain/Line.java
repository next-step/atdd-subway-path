package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
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
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Stations stations = new Stations(sections.getStations());
        SectionAddType addType = stations.findAddType(upStation, downStation);
        if (addType == SectionAddType.FIRST) {
            sections.addFirst(this, upStation, downStation, distance);
        } else if (addType == SectionAddType.LAST) {
            sections.addLast(this, upStation, downStation, distance);
        } else {
            sections.addMiddle(this, upStation, downStation, distance);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        sections.removeLast(station);
    }
}
