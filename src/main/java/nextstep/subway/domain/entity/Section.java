package nextstep.subway.domain.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section implements Comparable<Section> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineId")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStationId")
    private Station downStation;

    private long distance;

    public Section(Line line, Station upStation, Station downStation, long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, long distance) {
        return new Section(line, upStation, downStation, distance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void minusDistance(long distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("must be smaller than exists");
        }
        this.distance -= distance;
    }

    public void plusDistance(long distance) {
        this.distance += distance;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }
}
