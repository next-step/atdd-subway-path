package nextstep.subway.domain;

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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        this.sections.updateSection(section);
        this.sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Station station) {
        validateOnlyOneSection();
        validateStationInLine(station);

        List<Section> findSections = this.sections.findSectionsByStation(station);

        mergeSection(findSections);
        removeSections(findSections);
    }

    private void mergeSection(List<Section> findSections) {
        if (findSections.size() == 2) {
            this.sections.addMergeSection(this, findSections.get(0), findSections.get(1));
        }
    }

    private void removeSections(List<Section> findSections) {
        findSections.forEach(section -> this.sections.removeSection(section));
    }

    private void validateOnlyOneSection() {
        if (this.sections.isOnlyOne()) {
            throw new IllegalStateException("등록된 구간이 딱 한개면 구간을 삭제할 수 없습니다.");
        }
    }

    private void validateStationInLine(Station station) {
        if (!this.sections.hasStation(station)) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역은 제거할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
