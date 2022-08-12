package nextstep.subway.domain.line;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.Stations;
import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;

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

    public Sections getSections() {
        return sections;
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public Stations getStations() {
        if (this.sections.isEmpty()) {
            return new Stations(Collections.emptyList());
        }

        // Todo: upStationSection 변수 final 로 리팩터링
        // upStationSection 을 final 로 정의하고 싶은데 재귀함수를 구현하는데에 시간이 너무 많이 소요되어
        // 해당 부분은 우선 final 을 쓰지 않는 방향으로 작성하겠습니다..
        final Stations stations = new Stations(new ArrayList<>());
        Section upStationSection = this.sections.getFirstSection();
        stations.add(upStationSection);

        while (true) {
            Section nextSection = this.sections.getNextSection(upStationSection);
            if (nextSection == null) {
                break;
            }
            stations.add(nextSection.getDownStation());
            upStationSection = nextSection;
        }
        return stations;
    }
}
