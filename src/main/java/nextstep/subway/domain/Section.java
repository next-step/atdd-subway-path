package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Getter
@Table(name = "subway_section")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section extends BaseEntity {
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

    protected Section(Long distance, Station upStation, Station downStation) {
        super();
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public void assignSubwayLine(SubwayLine subwayLine) {
        this.subwayLine = subwayLine;
    }

    public void updateDownStation(Station downStation, Long newDistance){
        this.downStation = downStation;
        if(distance<= 0) throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다");
        this.distance = newDistance;
    }

    public void updateUpStation(Station upStation, Long newDistance){
        this.upStation = upStation;
        if(distance<= 0) throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다");
        this.distance = newDistance;
    }

    public boolean isUpStationId(Long id) {
        return this.upStation.getId().equals(id);
    }

    public boolean isDownStationId(Long id) {
        return this.downStation.getId().equals(id);
    }

    public Optional<Long> getUpStationId() {
        return this.upStation.getSafeId();
    }

    public Optional<Long> getDownStationId() {
        return this.downStation.getSafeId();

    }
}
