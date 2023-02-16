package nextstep.subway.domain;

import javax.persistence.*;
import lombok.Getter;

@Entity
@Getter
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

    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }


    public void reduceDistance(int distance) {
        this.distance -= distance;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public boolean equalsUpStation(Station upstation) {
        return this.getUpStation().getId() == upstation.getId();
    }

    public boolean equalsDownStation(Station downStation) {
        return this.getDownStation().getId() == downStation.getId();
    }
}