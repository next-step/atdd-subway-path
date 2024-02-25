package nextstep.subway.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor()
@Entity
public class Section implements Comparable<Section>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    @Builder
    public Section(Station upStation, Station downStation, Long distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    @Override
    public int compareTo(Section section) {
        if (this.equals(section)) {
            return 0;
        }
        if (this.downStation.equals(section.upStation)) {
            return -1;
        }
        return 1;
    }

    public boolean hasUpStationWithId(Long stationId) {
        return this.upStation.getId().equals(stationId);
    }

    public boolean hasDownStationWithId(Long stationId) {
        return this.downStation.getId().equals(stationId);
    }
}
