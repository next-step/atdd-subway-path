package nextstep.subway.line;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        /*
         * newSection의 upstation이 line의 하행종점역이면
         * addLastSection() 호출
         * newSection의 upstation이 line의 상행종점역이면
         * addFirstSection() 호출
         * 이 둘 중 아무것도 아니면
         * addSection() 호출
         * */

        if (section.getDownstation().getId() == sections.getFirstUpstation().getId()) {
            log.info("sections.addFirstSection()");
            sections.addFirstSection(section);
        } else if(section.getUpstation().getId() == sections.getLastDownstation().getId()) {
            log.info("sections.addLastSection()");
            sections.addLastSection(section);
        } else {
            log.info("sections.addSection()");
            sections.addSection(section);
        }

        distance += section.getDistance();
    }

    public void popSection(Station station) {
        int lastSectionDistance = sections.getLastSectionDistance();
        distance -= lastSectionDistance;
        sections.popSection(station);
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
