package nextstep.subway.domain.line.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.domain.Station;
import nextstep.subway.global.exception.GlobalException;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Integer distance;

    @Builder
    private Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section create(Line line, Station upStation, Station downStation, Integer distance) {
        if (!line.isStationDirectionEqual(upStation.getId())) {
            throw new GlobalException("새로운 구간의 상행역은 해당 노선의 하행 종점역과 일치해야합니다.");
        }

        if (!line.containsSectionByStation(downStation.getId())) {
            throw new GlobalException("이미 존재하는 역입니다.");
        }

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    public boolean isDownStation(Long stationId) {
        return this.downStation.isStationId(stationId);
    }
}
