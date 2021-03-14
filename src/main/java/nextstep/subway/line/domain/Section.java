package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Section implements Comparable<Section>{

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

    @Enumerated
    private Distance distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Line line, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public Distance getDistance() {
        return distance;
    }

    public boolean upAndDownStationMatch(Station upStation, Station downStation) {
        return this.upStation.equals(upStation) && this.downStation.equals(downStation);
    }

    public List<Station> getStations() {
        return Stream.of(upStation, downStation).collect(Collectors.toList());
    }

    @Override
    public int compareTo(final Section section) {
        if (section.getDownStation().equals(this.downStation)) {
            return 0;
        }

        if (section.getDownStation().equals(this.upStation)) {
            return 1;
        }

        return -1;
    }

    public static final class Builder {
        private Line line;
        private Station upStation;
        private Station downStation;
        private Distance distance;

        private Builder() {
        }

        public static Builder aSection() {
            return new Builder();
        }

        public Builder line(Line line) {
            this.line = line;
            return this;
        }

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Builder distance(Distance distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(line, upStation, downStation, distance);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        return new EqualsBuilder().append(distance, section.distance).append(id, section.id).append(line, section.line).append(upStation, section.upStation).append(downStation, section.downStation).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(line).append(upStation).append(downStation).append(distance).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("line", line)
                .append("upStation", upStation)
                .append("downStation", downStation)
                .append("distance", distance)
                .toString();
    }
}
