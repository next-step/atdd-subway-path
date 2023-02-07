package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int order;

    private int distance;

    public Long getDownStationId() {
        return Optional.ofNullable(downStation)
                .orElseThrow(() -> new IllegalStateException("하행역이 없습니다."))
                .getId();
    }

    public Long getUpStationId() {
        return Optional.ofNullable(upStation)
                .orElseThrow(() -> new IllegalStateException("상행역이 없습니다."))
                .getId();
    }

    public void changeLine(Line newLine) {
        if (line != null && line.equals(newLine)) {
            return;
        }

        if (line != null) {
            line.remove(this);
        }

        newLine.addSection(this);
        line = newLine;
    }

    public void removeLine() {
        if (line == null) {
            return;
        }

        line.remove(this);
        line = null;
    }

    public void minusDistacne(int distance) {
        this.distance -= distance;
    }

    public boolean isDistanceGreaterThen(int distance) {
        return this.distance > distance;
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownUpStation(Station station) {
        return this.downStation.equals(station);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeOrder(int order) {
        this.order = order;
    }
}