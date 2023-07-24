package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "up_station_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Station upStation;

    @JoinColumn(name = "down_station_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Station downStation;

    @Column(nullable = false)
    private Long distance;


    protected Section() {
    }

    private Section(Station upStation, Station downStation, Long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Long distance) {
        return new Section(upStation, downStation, distance);
    }

    public Long getId() {
        return this.id;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public boolean isDifferentAs(Section section) {
        return !isSameAs(section);
    }

    public boolean isSameAs(Section section) {
        return (isUpStation(section.getUpStation()) && isDownStation(section.getDownStation()))
                || isUpStation(section.getDownStation()) && isDownStation(section.getDownStation());
    }

    public boolean isExpandable(Section section) {
        return isUpStation(section.getUpStation())
                || isDownStation(section.getUpStation())
                || isUpStation(section.getDownStation())
                || isDownStation(section.getDownStation());
    }

    public boolean isUpStation(Station station) {
        return Objects.equals(this.upStation, station);
    }

    public boolean isDownStation(Station station) {
        return Objects.equals(this.downStation, station);
    }

    public boolean isEqualsOrShorterThan(Section newSection) {
        return this.distance <= newSection.getDistance();
    }
}
