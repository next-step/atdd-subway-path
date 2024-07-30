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
public class Section extends BaseEntity{
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
