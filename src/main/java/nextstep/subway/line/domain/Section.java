package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Builder builder) {
        id = builder.id;
        line = builder.line;
        upStation = builder.upStation;
        downStation = builder.downStation;
        distance = builder.distance;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public Section updateLine(Line line) {
        this.line = line;
        return this;
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

    public static class Builder{
        private Long id;
        private Line line;
        private Station upStation;
        private Station downStation;
        private int distance;

        public Builder id(Long val) {
            this.id = val;
            return this;
        }

        public Builder line(Line val) {
            this.line = val;
            return this;
        }

        public Builder upStation(Station val) {
            this.upStation = val;
            return this;
        }

        public Builder downStation(Station val) {
            this.downStation = val;
            return this;
        }

        public Builder distance(int val) {
            this.distance = val;
            return this;
        }

        public Section build() {
            return new Section(this);
        }

    }
}
