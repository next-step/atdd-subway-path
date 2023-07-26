package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Integer distance;

    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color, Station upStation, Station downStation, Integer distance, Section section) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        sections = new Sections(section, this);
    }

    public static Line of(String name, String color, Station upStationId, Station downStationId, Integer distance) {
        return Line.builder()
                .name(name)
                .color(color)
                .upStation(upStationId)
                .downStation(downStationId)
                .distance(distance)
                .build();
    }

    public void modifySubwayLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section, this);
    }

    public void removeSection(Station downStation) {
        sections.removeSection(downStation, this);
    }

    public Set<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        // 첫번째 구간 찾기
        Section firstSection = sections.getFirstSection();
        stations.add(firstSection.getUpStation());

        // 첫번째 구간 외 나머지 구간 찾기
        Section nextSection = firstSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = sections.getNextSection(nextSection);
        }

        return stations;
    }

    public void modifyDistance() {
        this.distance = sections.getSections().stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
