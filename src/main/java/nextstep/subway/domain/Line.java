package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedDownStationException;
import nextstep.subway.exception.NotEqualLastStationException;
import nextstep.subway.exception.NotLastStationException;
import nextstep.subway.exception.SingleSectionException;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }

        if (!sections.isLastStation(upStation)) {
            throw new NotEqualLastStationException();
        }

        if (sections.containsStation(downStation)) {
            throw new DuplicatedDownStationException();
        }
        sections.add(new Section(this, upStation, downStation, distance));
    }


    public int getSumDistance() {
        return sections.calcDistance();
    }


    public List<Station> getAllStations() {
        return sections.getStations();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void removeSection(Station station) {
        if (!sections.isLastStation(station)) {
            throw new NotLastStationException();
        }
        if (sections.isSingleSection()) {
            throw new SingleSectionException();
        }
        sections.remove();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }
}
