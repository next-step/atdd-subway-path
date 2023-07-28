package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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
        if (!sections.getSections().isEmpty() && !sections.getStations().contains(upStation) && !sections.getStations().contains(downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (sections.getStations().contains(upStation) && sections.getStations().contains(downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }

        Optional<Section> existSection = sections.getSections().stream()
                .filter(section -> section.hasStation(upStation, downStation))
                .findAny();
        if (existSection.isPresent() && existSection.get().getDistance() <= distance) {
            throw new SectionAddException(ErrorType.SECTION_DISTANCE_TOO_LONG);
        }
        sections.add(this, upStation, downStation, distance);
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
