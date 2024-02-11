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

    public boolean canSectionDelete(Long stationId) {
        if (!sections.findLastStation().getId().equals(stationId)) {
            return false;
        }
        return sections.isDeletionAllowed();
    }


    public void addSection(Section createdSection) {
        if (canSectionSave(createdSection)) {
            sections.addSection(createdSection);
        }
    }

    public void deleteSection(Station stationToDelete) {
        this.sections.deleteSection(stationToDelete);
    }

    private boolean canSectionSave(Section toSaveSection) {
        if (toSaveSection.areStationsSame()) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역은 동일할 수 없습니다.");
        }
        if (sections.hasNoSections()) {
            return true;
        }
        if (!sections.isConnectToLastStation(toSaveSection)) {
            throw new IllegalArgumentException("추가할 구간의 상행역이 기존 노선의 하행역과 동일하지 않습니다.");
        }
        if (hasExistingStation(toSaveSection)) {
            throw new IllegalArgumentException("이미 노선에 추가된 구간을 추가할 수 없습니다.");
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
}
