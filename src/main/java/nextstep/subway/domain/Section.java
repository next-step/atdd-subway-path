package nextstep.subway.domain;

import static com.google.common.base.Preconditions.checkArgument;

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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;

        checkArgument(distance > 0);
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Section{");
        sb.append("line=").append(line);
        sb.append(", upStation=").append(upStation);
        sb.append(", downStation=").append(downStation);
        sb.append(", distance=").append(distance);
        sb.append('}');
        return sb.toString();
    }

    public boolean isSameAsSection(Section section) {
        return isSameAsUpStationAndDownStation(section.upStation, section.downStation) ||
            isSameAsUpStationAndDownStation(section.downStation, section.upStation);
    }

    private boolean isSameAsUpStationAndDownStation(Station upStation, Station downStation) {
        return isSameAsUpStation(upStation) && isSameAsDownStation(downStation);
    }

    public boolean isSameAsUpStation(Station station) {
        return upStation == station;
    }

    public boolean isSameAsDownStation(Station station) {
        return downStation == station;
    }
}
