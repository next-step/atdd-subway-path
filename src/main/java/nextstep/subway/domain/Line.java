package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

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
        List<Station> stations = sections.getStations();
        if (!stations.isEmpty() && !stations.contains(upStation) && !stations.contains(downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }

        if (!stations.isEmpty() && stations.get(0).equals(downStation)) {
            sections.addFirst(this, upStation, downStation, distance);
        } else if (!stations.isEmpty() && stations.get(stations.size() - 1).equals(upStation)) {
            sections.addLast(this, upStation, downStation, distance);
        } else {
            sections.add(this, upStation, downStation, distance);
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
