package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionNonExistException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_NOT_FOUND_SECTION;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.addSection(section);
    }

    public void deleteSection(Station deleteStation) {
        sections.deleteSection(deleteStation);
    }

    public Section findSection(Station upStation, Station downStation) {
        return sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(upStation) && section.getDownStation().equals(downStation))
                .findFirst()
                .orElseThrow(() -> new SectionNonExistException(EXCEPTION_MESSAGE_NOT_FOUND_SECTION));
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
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
}
