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

    public boolean canSectionSave(Section toSaveSection) {
        if(toSaveSection.areStationsSame()) {
            return false;
        }
        if(!sections.isConnectToLastStation(toSaveSection)) {
            return false;
        }
        return hasNoConnectingDownStation(toSaveSection);
    }

    private boolean hasNoConnectingDownStation(Section toSaveSection) {
        return sections.areAllUpStationsDifferentFrom(toSaveSection);
    }

    public boolean canSectionDelete(Long stationId) {
        if(!sections.findLastStation().getId().equals(stationId)) {
            return false;
        }
        return sections.isDeletionAllowed();
    }


    public void addSection(Section createdSection) {
        sections.addSection(createdSection);
    }

    public void deleteSection(Station stationToDelete) {
        this.sections.deleteSection(stationToDelete);
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
}
