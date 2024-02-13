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

    private int distance;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line update(String name, String color) {
        this.name = name;
        this.color = color;
        return this;
    }

    private boolean hasExistingStation(Section toSaveSection) {
        return sections.hasExistingStationExceptLast(toSaveSection);
    }

    public void addSection(Section createdSection) {
        if (canSectionAdd(createdSection)) {
            sections.addSection(createdSection);
        }
    }

    public void deleteSection(Station stationToDelete) {
        if(sections.canSectionDelete(stationToDelete)) {
            sections.deleteLastSection();
        }
    }

    private boolean canSectionAdd(Section toSaveSection) {
        if (toSaveSection.areStationsSame()) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역은 동일할 수 없습니다.");
        }
        if (sections.hasNoSections()) {
            return true;
        }
        return true;
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

    public int getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getAllStations();
    }

    public List<Section> getAllSections() {
        return sections.getAllSections();
    }
}
