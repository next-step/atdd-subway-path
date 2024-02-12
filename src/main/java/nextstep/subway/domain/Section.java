package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer orderNo;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance,
        Integer orderNo) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.orderNo = orderNo;
    }

    public static Section createMiddleSection(Line line, Station upStation, Station downStation,
        int distance, int orderNo) {
        return new Section(line, upStation, downStation, distance, orderNo);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public static Section createFirstSection(Line line, Station upStation, Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, 1);
    }

    public static Section createTempSection(Line line, Station upStation, Station downStation,
        int distance) {
        return new Section(line, upStation, downStation, distance, null);
    }

    public void changeUpStation(Station downStation) {
        this.upStation = downStation;
    }

    public void changeOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public void changeDistance(int distance) {
        if (distance == 0) {
            throw new IllegalArgumentException();
        }
        this.distance = distance;
    }
}
