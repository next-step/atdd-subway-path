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


    public void addSection(Section createdSection) {
        if (canSectionAdd(createdSection)) {
            sections.addSection(createdSection);
        }
    }

    public void deleteSection(Station stationToDelete) {
        if (sections.canSectionDelete(stationToDelete)) {
            sections.deleteLastSection();
        }
    }

    private boolean canSectionAdd(Section sectionToAdd) {
        if (sectionToAdd.areStationsSame()) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역은 동일할 수 없습니다.");
        }
        if (sections.hasNoSections()) {
            return true;
        }
        if (hasExistingDownStation(sectionToAdd.getDownStation())) {
            throw new IllegalArgumentException("이미 노선에 추가된 하행역입니다.");
        }
        if (!hasExistingStation(sectionToAdd.getUpStation())) {
            throw new IllegalArgumentException("추가할 구간의 상행역이 노선에 존재하지 않습니다.");
        }
        return true;
    }

    private boolean hasExistingStation(Station upStation) {
        return sections.hasExistingStation(upStation);
    }

    private boolean hasExistingDownStation(Station downStation) {
        return sections.hasExistingDownStation(downStation);
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
