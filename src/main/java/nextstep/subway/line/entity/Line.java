package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
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
        // 이미 등록된 구간인지 확인
        if (sections.alreadySection(section)) {
            throw new SubwayException(ErrorCode.ALREADY_SECTION);
        }

        // 등록하려는 구간이 기존 구간에 포함되는지 확인
        if (!sections.cannotAddSection(section)) {
            throw new SubwayException(ErrorCode.CAN_NOT_BE_ADDED_SECTION);
        }

        // 역 사이에 새로운 역을 등록할 경우
        if (sections.isUptoUp(section)) {
            sections.addNewStationBetweenExistingStation(section, this);
            return;
        }

        // 새로운 역을 상행 종점으로 등록할 경우
        if (sections.isUpToDown(section)) {
            sections.addNewStationAsAnUpStation(section);
            return;
        }

        // 새로운 역을 하행 종점으로 등록할 경우
        if (sections.isDownToUp(section)) {
            sections.addNewStationAsAnDownStation(section);
        }
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
