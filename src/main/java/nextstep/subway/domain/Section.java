package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subway_section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private SubwayLine subwayLine;

    private Section(Long distance, Station upStation, Station downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Section of(Long distance, Station upStation, Station downStation) {
        return new Section(distance, upStation, downStation);
    }

    public void assignSubwayLine(SubwayLine subwayLine) {
        this.subwayLine = subwayLine;
    }

    public boolean isUpStationId(Long id) {
        return this.upStation.getId().equals(id);
    }

    public boolean isDownStationId(Long id) {
        return this.downStation.getId().equals(id);
    }

    public Long getUpStationId() {
        return this.upStation.getId();
    }

    public Long getDownStationId() {
        return this.downStation.getId();
    }
}
