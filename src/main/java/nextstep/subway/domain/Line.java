package nextstep.subway.domain;

import javax.persistence.*;
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
    private Sections sections = new Sections();

    @Transient
    private Section firstSection = new Section();
    @Transient
    private Section lastSection = new Section();

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

    public Sections sections() {
        return this.sections;
    }

    public void firstAddSection(Station upStation, Station downStation, int distance) {
        final Section section = new Section(this, upStation, downStation, distance);
        this.sections.firstAddSection(section);
        this.firstSection = section;
        this.lastSection = section;
    }

    public void addSection(Section section) {
        this.addSection(section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public void addSection(Station upStation, Station downStation, int distance) {

//        if (this.sections.existUpStations(upStation)) {
//            Section getSection = this.sections.getSectionFromUpStation(upStation);
//            if (this.sections.isFirst(getSection)) {
//                this.firstSection = getSection;
//            }
//        }
//
//        if (this.sections.existDownStations(downStation)) {
//            Section getSection = this.sections.getSectionFromDownStation(downStation);
//            if (this.sections.isLast(getSection)) {
//                this.lastSection = getSection;
//            }
//        }

        this.sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getAllStations() {

        return sections.getAllStations();
//        List<Station> resultStations = new ArrayList<>();
//        resultStations.add(this.firstSection.getUpStation());
//        final Station currentDownStation = this.firstSection.getDownStation();
//        resultStations.add(currentDownStation);
//
//        Section sectionFromUpStation = this.sections.getSectionFromUpStation(currentDownStation);
//        while (true) {
//            final Station downStation = sectionFromUpStation.getDownStation();
//            resultStations.add(downStation);
//
//            final Section afterSection = this.sections.getSectionFromUpStation(downStation);
////            sectionFromUpStation = afterSection;
//
//            if(afterSection.getDownStation().equals(downStation)) {
//                break;
//            }
//        }
//        return resultStations;

//        final Section sectionFromDownStation = this.sections.getSectionFromDownStation(firstSection.getDownStation());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor()) && Objects.equals(sections, line.sections) && Objects.equals(firstSection, line.firstSection) && Objects.equals(lastSection, line.lastSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), sections, firstSection, lastSection);
    }
}
