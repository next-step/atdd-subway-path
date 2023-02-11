package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;

        Section section = new Section(this, upStation, downStation, distance);
        this.sections.add(section);
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
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section newSection = new Section(this, upStation, downStation, distance);

        Optional<Section> sameUpStationSection = findUpToUp(upStation);
        if(sameUpStationSection.isPresent()) {
            addUpToUp(sameUpStationSection.get(), newSection);
            return;
        }

        Optional<Section> sameDownStationSection = findDownToDown(downStation);
        if(sameDownStationSection.isPresent()) {
            addDownToDown(sameDownStationSection.get(), newSection);
            return;
        }

        if(hasDownToBeginUp(downStation)) {
            addDownToBeginUp(newSection);
            return;
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if(sections.isEmpty()) {
            return stations;
        }

        stations.add(getFirstSection().getUpStation());

        sections.forEach((it) -> stations.add(it.getDownStation()));

        return stations;
    }

    private Optional<Section> findUpToUp(Station upStation) {
        return sections.stream()
                .filter(it -> upStation.equals(it.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findDownToDown(Station downStation) {
        return sections.stream()
                .filter(it -> downStation.equals(it.getDownStation()))
                .findFirst();
    }

    private boolean hasDownToBeginUp(Station downStation) {
        Section beginSection = sections.stream().findFirst().get();
        return downStation.equals(beginSection.getUpStation());
    }

    private void addUpToUp(Section oldSection, Section newSection) {
        sections.add(newSection);
        sections.add(
                new Section(this,
                        newSection.getDownStation(),
                        oldSection.getDownStation(),
                        oldSection.getDistance() - newSection.getDistance())
        );
        sections.remove(oldSection);
    }

    private void addDownToDown(Section oldSection, Section newSection) {
        sections.remove(oldSection);
        sections.add(
                new Section(this,
                        oldSection.getUpStation(),
                        newSection.getUpStation(),
                        oldSection.getDistance() - newSection.getDistance())
        );
        sections.add(newSection);
    }

    private void addDownToBeginUp(Section newSection) {
        sections.add(0, newSection);
    }

    private Section getFirstSection() {
        return sections.get(0);
    }
}
