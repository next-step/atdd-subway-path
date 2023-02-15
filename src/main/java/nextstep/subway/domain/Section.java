package nextstep.subway.domain;

import lombok.*;
import nextstep.subway.exception.AlreadyAddedLineException;

import javax.persistence.*;
import java.util.Optional;

@ToString
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

    @Column(name = "order_seq")
    private int orderSeq;

    private int distance;

    public void addLine(Line line) {
        if (this.line != null) {
            throw new AlreadyAddedLineException();
        }

        this.line = line;
    }

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

    public boolean hasStationId(Long stationId) {
        return isSameUpStationId(stationId) || isSameDownStationId(stationId);
    }

    public boolean isSameUpStationId(Long stationId) {
        return getUpStationId().equals(stationId);
    }

    private boolean isSameDownStationId(Long stationId) {
        return getDownStationId().equals(stationId);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeOrder(int orderSeq) {
        this.orderSeq = orderSeq;
    }


    public boolean isFirst() {
        return orderSeq == 0;
    }

    public void plusDistance(int distance) {
        this.distance += distance;
    }

    public void deleteMiddleStation(Section target) {
        plusDistance(target.getDistance());
        this.downStation = target.getDownStation();
    }

    public boolean isLast(int size) {
        return orderSeq == size - 1;
    }
}