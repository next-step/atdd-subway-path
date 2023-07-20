package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Builder
    public Line(String name, String color, Station upStation, Station downStation, Integer distance, Section section) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.addSectionInit(section);
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

    public void addSectionInit(Section section) {
        this.sections.add(section);
        section.addSection(this);
    }

    public void addSection(Section section) {
        // 역 사이에 새로운 역을 등록할 경우
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    this.sections.add(Section.of(section.getDownStation(), oldSection.getDownStation(), validationDistance(oldSection.getDistance(), section.getDistance()), this));
                    this.sections.remove(oldSection);
                });

        // 새로운 역을 상행 종점으로 등록할 경우
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));

        // 새로운 역을 하행 종점으로 등록할 경우
        sections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));
    }

    public void isExistsDownStation(Station upStation) {
        if (!this.downStation.equals(upStation)) {
            throw new SubwayException(ErrorCode.INVALID_UP_STATION);
        }
    }

    public void isExistsStations(Station station) {
        if (Arrays.asList(upStation, downStation).contains(station)) {
            throw new SubwayException(ErrorCode.INVALID_DOWN_STATION);
        }
    }

    public void removeSection(Station downStation) {
        if (isSectionOne()) {
            throw new SubwayException(ErrorCode.SECTION_IS_ONE);
        }
        if (!isLastSection(downStation)) {
            throw new SubwayException(ErrorCode.NOT_DOWN_STATION);
        }
        this.sections.removeIf(section -> section.isDownStation(downStation));
    }

    private boolean isSectionOne() {
        return this.sections.size() == 1;
    }

    private boolean isLastSection(Station downStation) {
        return getLastSection()
                .map(section -> section.isDownStation(downStation))
                .orElse(false);
    }

    private Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(sections.get(sections.size() - 1));
        }
    }

    private int validationDistance(int oldDistance, int newDistance) {
        int distance = oldDistance - newDistance;
        if (distance <= 0) {
            throw new SubwayException(ErrorCode.INVALID_DISTANCE);
        }
        return distance;
    }

    public void validationStations(Station upStation, Station downStation) {
        // 신규 구간의 상행역이 기존 구간의 하행역과 같은지 확인 아닐시 예외 처리
        isExistsDownStation(upStation);

        // 신규 구간의 하행역이 기존 구간의 역이 있는지 검사 있으면 예외 처리
        isExistsStations(downStation);
    }
}
