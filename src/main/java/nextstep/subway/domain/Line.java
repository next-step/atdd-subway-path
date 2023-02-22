package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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

        Optional<Section> firstSection =  this.sections.findSectionByDownStation(station);
        Optional<Section> secondSection =  this.sections.findSectionByUpStation(station);

        validateStationInLine(firstSection.isEmpty() && secondSection.isEmpty());

        mergeSection(firstSection, secondSection);
        removeSection(firstSection, secondSection);
    }

    private void removeSection(Optional<Section> firstSection, Optional<Section> secondSection) {
        firstSection.ifPresent(section -> this.sections.removeSection(section));
        secondSection.ifPresent(section -> this.sections.removeSection(section));
    }

    private void mergeSection(Optional<Section> firstSection, Optional<Section> secondSection) {
        if (firstSection.isPresent() && secondSection.isPresent()) {
            this.sections.addMergeSection(this, firstSection.get(), secondSection.get());
        }
    }

    private void validateOnlyOneSection() {
        if (this.sections.isOnlyOne()) {
            throw new IllegalStateException("등록된 구간이 딱 한개면 구간을 삭제할 수 없습니다.");
        }
    }

    private void validateStationInLine(boolean isStationNotInLine) {
        if (isStationNotInLine) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역은 제거할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
