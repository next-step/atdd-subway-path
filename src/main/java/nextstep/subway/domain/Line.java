package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne
    private Station firstStation;

    @ManyToOne
    private Station lastStation;

    @Embedded
    private Sections sections;

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

    public Sections getSections() {
        return sections;
    }

    public boolean hasSections() {
        return sections != null && sections.hasSections();
    }

    public void addSection(Section section) {
        if (!hasSections()) {
            sections = new Sections(section);
            firstStation = section.getUpStation();
            lastStation = section.getDownStation();
            return;
        }

        sections.addSection(this, section);
    }

    public int getSectionsCount() {
        return sections.getSectionsCount();
    }

    public Section getFirstSection() {
        return sections.getFirstSection();
    }

    public List<Station> getStations() {
        if (!hasSections()) {
            return new ArrayList<>();
        }

        Set<Station> stations = new LinkedHashSet<>();

        Section currSection = getFirstSection();
        while (currSection != null) {
            stations.add(currSection.getUpStation());
            stations.add(currSection.getDownStation());
            currSection = sections.getNextSection(currSection);
        }

        return new ArrayList<>(stations);
    }

    public void removeSection(Station station) {
        sections.removeSection(station, lastStation);
    }

    public int getLength() {
        if (!hasSections()) {
            return 0;
        }

        return sections.getTotalDistance();
    }

    public Station getFirstStation() {
        return firstStation;
    }

    public Station getLastStation() {
        return lastStation;
    }

    public void changeFirstStation(Station station) {
        firstStation = station;
    }

    public void changeLastStation(Station station) {
        lastStation = station;
    }

}
