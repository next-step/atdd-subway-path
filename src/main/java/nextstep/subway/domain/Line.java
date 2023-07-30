package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.domain.SectionException;

import javax.persistence.*;
import javax.servlet.ServletContainerInitializer;
import java.util.ArrayList;
import java.util.List;

@Entity
public class  Line {

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
        return getStartSection().getUpStation();
    }

    public Station getLastStation() {
        return getLastSection().getDownStation();
    }

    public List<Section> getSections() {
        return sections;
    }

    public int getSectionsLastIndex() { return sections.size()-1; }

    public Section getStartSection() {
        return this.sections.get(0);
    }

    public Section getLastSection() { return sections.get(getSectionsLastIndex()); }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Line addSectionAtStart(Section newSection) {
        Section startSection = getStartSection();
        startSection.updateSection(newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance());
        this.sections.add(newSection);
        return this;
    }

    public Line addSectionAtEnd(Section newSection) {
        this.sections.add(newSection);
        return this;
    }

    public Line addSectionAtMiddle(Section newSection) {
        for (Section savedSection : this.sections) {
            if (savedSection.getUpStation().checkEqualStationByName(newSection.getUpStation())) {
                checkValidDistance(savedSection, newSection);
                this.sections.add(0, newSection);
                int calculatedDistance = savedSection.getDistance() - newSection.getDistance();
                savedSection.updateSection(newSection.getDownStation(), savedSection.getDownStation(), calculatedDistance);
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
        for (Section savedSection : this.sections) {
            if (savedSection.getUpStation().checkEqualStationByName(newStation)) {
                return true;
            } else if(savedSection.getDownStation().checkEqualStationByName(newStation)) {
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

    public void deleteSectionAtStart() {
        this.sections.remove(0);
    }

    public void deleteSectionAtMiddle(Station deletedStation) {
        Section preSection = this.findPreSectionByStation(deletedStation);
        Section nextSection = this.findNextSectionByStation(deletedStation);
        int calculatedDistance = preSection.getDistance() + nextSection.getDistance();

        preSection.updateSection(preSection.getUpStation(), nextSection.getDownStation(), calculatedDistance);
        this.sections.remove(nextSection);
    }

    public void deleteSectionAtLast() {
        this.sections.remove(getSectionsLastIndex());
    }

    private Section findPreSectionByStation(Station station) {
        for (Section savedSection : this.sections) {
            if (savedSection.getDownStation().checkEqualStationByName(station)) {
                return savedSection;
            }
        }
        throw new SectionException("Section is not existed by requested station");
    }

    private Section findNextSectionByStation(Station station) {
        for (Section savedSection : this.sections) {
            if (savedSection.getUpStation().checkEqualStationByName(station)) {
                return savedSection;
            }
        }
        throw new SectionException("Section is not existed by requested station");
    }
}