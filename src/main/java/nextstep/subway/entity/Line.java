package nextstep.subway.entity;

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
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line update(String name, String color) {
        this.name = name;
        this.color = color;
        return this;
    }


    public void addSection(Section createdSection) {
        if (canSectionAdd(createdSection)) {
            sections.addSection(createdSection);
        }
    }

    public void delete(Station stationToDelete) {
        if (sections.canSectionDelete(stationToDelete)) {
            sections.deleteSection(this, stationToDelete);
        }
    }

    private boolean canSectionAdd(Section sectionToAdd) {
        if (sectionToAdd.areStationsSame()) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역은 동일할 수 없습니다.");
        }
        if (sections.hasNoSections()) {
            return true;
        }
        if (hasStations(sectionToAdd)) {
            throw new IllegalArgumentException("이미 노선에 포함되어 있는 상행역과 하행역입니다.");
        }
        if (!hasExactlyOneStation(sectionToAdd)) {
            throw new IllegalArgumentException("노선에 연결할 수 있는 상행역 혹은 하행역이 아닙니다.");
        }
        return true;
    }

    private boolean hasExactlyOneStation(Section sectionToAdd) {
        return hasExistingStation(sectionToAdd.getUpStation()) ^
                hasExistingStation(sectionToAdd.getDownStation());
    }

    private boolean hasStations(Section sectionToAdd) {
        return hasExistingStation(sectionToAdd.getUpStation()) &&
                hasExistingStation(sectionToAdd.getDownStation());
    }

    private boolean hasExistingStation(Station station) {
        return sections.hasExistingStation(station);
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

    public List<Station> getAllStations() {
        return sections.getAllStations();
    }

    public List<Section> getAllSections() {
        return sections.getAllSections();
    }
}
