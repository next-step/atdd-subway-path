package nextstep.subway.domain;

import javax.persistence.*;

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

    private int distance;

    public Section() {

    }

    public Section(SectionBuilder builder) {
        this.line = builder.line;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = builder.distance;
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

    public boolean equalUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public static class SectionBuilder {
        private Line line;
        private Station upStation;
        private Station downStation;
        private int distance;

        public SectionBuilder(Line line) {
            this.line = line;
        }

        public SectionBuilder setUpStation(Station station) {
            this.upStation = station;
            return this;
        }

        public SectionBuilder setDownStation(Station station) {
            this.downStation = station;
            return this;
        }

        public SectionBuilder setDistance(int distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(this);
        }

    }


}
