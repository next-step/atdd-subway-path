package nextstep.subway.domain;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.SectionCollection;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private SectionCollection sectionCollection = new SectionCollection();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
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

    public void addSections(Section section) {
        sectionCollection.addSection(section);
    }

    public void addSections(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sectionCollection.addSection(section);
    }

    public List<Station> getStations() {
        return sectionCollection.getStations();
    }

    public void removeStation(Station station) {
        sectionCollection.removeSectionCollection(station);
    }

    public boolean isEmptyStations() {
        return sectionCollection.isEmpty();
    }

    public Station getLastStation() {
        return sectionCollection.getLastStation();
    }
    public Station getFirstStation() {
        return sectionCollection.getFirstStation();
    }

    public Optional<Section> getUpSection(Station station) {
        return sectionCollection.getUpSection(station);
    }

    public Optional<Section> getDownSection(Station station) {
        return sectionCollection.getDownSection(station);
    }

}
