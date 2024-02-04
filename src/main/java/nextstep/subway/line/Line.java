package nextstep.subway.line;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nextstep.subway.exception.InvalidInputException;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Slf4j
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Sections sections = new Sections();

    @Column(nullable = false)
    private int distance;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void initSection(Station upstation, Station downstation) {
        Section section = Section.initSection(this, upstation, downstation);
        sections.initSection(section);
    }

    public void addSection(Section section) {
        if (section.getDownstation().getId() == sections.getFirstUpstation().getId()) {
            sections.addFirstSection(section);
            distance += section.getDistance();
        } else if (section.getUpstation().getId() == sections.getLastDownstation().getId()) {
            sections.addLastSection(section);
            distance += section.getDistance();
        } else {
            sections.addSection(section, distance);
        }
    }

    public void removeSection(Station station) {
        if (sections.size() == 1) {
            throw new InvalidInputException("노선에 상행 종점역과 하행 종점역만 있는 경우에는 제거할 수 없습니다.");
        }

        if (station.getId() == sections.getFirstUpstation().getId()) {
            int firstSectionDistance = sections.getFirstSectionDistance();
            distance -= firstSectionDistance;
            sections.removeFirstSection();
        } else if (station.getId() == sections.getLastDownstation().getId()) {
            int lastSectionDistance = sections.getLastSectionDistance();
            distance -= lastSectionDistance;
            sections.removeLastSection();
        } else {
            sections.removeSection(station);
        }
    }

    @Builder
    public Line(Long id, String color, String name, Sections sections, int distance) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.sections = sections != null ? sections : new Sections();
        this.distance = distance;
    }
}
