package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(final String name, final String color, final Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this(name, color, new Sections());
    }

    public void registerSection(final Station upStation, final Station downStation, final int distance) {
        sections.addSection(this, upStation, downStation, distance);
    }

    public void deleteSection(final Station station) {
        sections.deleteSection(station);
    }

    public void updateName(final String name) {
        this.name = name;
    }

    public void updateColor(final String color) {
        this.color = color;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        List<Section> sections = new ArrayList<>();
        Station upStation = this.sections.getSangHang();
        if(Objects.isNull(upStation)){
            return sections;
        }

        do {
            Section section = this.sections.getSectionByUpStation(upStation);
            sections.add(section);
            upStation = section.getDownStation();
        } while (!this.sections.isDownStationEndpoint(upStation));

        return sections;
    }
}
