package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.domain.SectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OrderColumn
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() { }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
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

    public String getColor() {
        return color;
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Station getStartStation() {
        List<Section> sections = getThisSections();
        return sections.get(sections.size()-1).getUpStation();
    }

    public Station getLastStation() {
        List<Section> sections = getThisSections();
        return sections.get(sections.size()-1).getDownStation();
    }

    public List<Section> getSections() {
        return sections;
    }

    private List<Section> getThisSections() { return this.sections; }

    public Section getStartSection() {
        return getThisSections().get(0);
    }

    public void addSection(Section section) {
        List<Section> sections = getThisSections();
        sections.add(section);
    }

    public Line addSectionAtStart(Section newSection) {
        Section startSection = getStartSection();
        startSection.updateSection(newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance());
        getThisSections().add(newSection);
        return this;
    }

    public Line addSectionAtEnd(Section newSection) {
        getThisSections().add(newSection);
        return this;
    }

    public Line addSectionAtMiddle(Section newSection) {
        for (Section savedSection : getThisSections()) {
            if (savedSection.getUpStation().getId().equals(newSection.getUpStation().getId())) {
                checkValidDistance(savedSection, newSection);
                getThisSections().add(0, newSection);
                savedSection.updateDistance(newSection.getDistance());
                break;
            }
        }
        return this;
    }

    private void checkValidDistance(Section savedSection, Section newSection) {
        if (newSection.getDistance() >= savedSection.getDistance()) {
            throw new SectionException("Requested distance is equal or larger than the saved distance.");
        }
    }

    public boolean checkDuplicatedStation(Station newStation) {
        for (Section savedSection : getThisSections()) {
            if (savedSection.getUpStation().checkEqualStation(newStation)) {
                return true;
            } else if(savedSection.getDownStation().checkEqualStation(newStation)) {
                return true;
            }
        }
        return false;
    }

    public void beforeAddSection(Section newSection) {
        if (!checkDuplicatedStation(newSection.getUpStation()) &&
                !checkDuplicatedStation(newSection.getDownStation())) {
            throw new SectionException("Requested stations is not saved");
        }
    }

    public void deleteSectionByUpStation(Station upStation) {
        List<Section> sections = getThisSections();

        for (Section savedSection : sections) {
            Station savedUpStation = savedSection.getUpStation();
            if (savedUpStation.getName().equals(upStation.getName())) {
                sections.remove(savedSection);
                return;
            }
        }
    }
}