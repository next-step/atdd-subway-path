package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.applicaion.exceptions.SectionNotEnoughException;
import nextstep.subway.enums.exceptions.ErrorCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Integer> getDistance() {
        return sections.getStationDistance();
    }

    public List<Station> getStation() {
        return sections.getStation();
    }

    public void removeSection(Station lastStation) {
        if (sections.getSections().size() < 2)
            throw new SectionNotEnoughException(ErrorCode.NOT_ENOUGH_SECTION);
        sections.removeSection(lastStation);
    }
}
