package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionDeleteException;

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
        SectionAddType addType = sections.findAddType(upStation, downStation);
        addType.apply(sections, new Section(this, upStation, downStation, distance));
    }

    public void updateName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateColor(String color) {
        if (color != null) {
            this.color = color;
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        if (sections.remainOneSection()) {
            throw new SectionDeleteException(ErrorType.CANNOT_REMOVE_LAST_SECTION);
        }
        Stations stations = new Stations(sections.getStations());
        SectionDeleteType deleteType = SectionDeleteType.find(stations, station);
        deleteType.apply(sections, station);
    }
}
