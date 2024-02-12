package nextstep.subway.line.section;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.exception.HttpBadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;


import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    private int distance;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        validation(upStation, downStation, line);
        setLine(line);
    }

    public void setLine(Line line) {
        if(!line.getSections().contains(this)){
            line.getSections().add(this);
        }
        this.line = line;
    }

    private void validation(Station upStation, Station downStation, Line line) {
        if(upStation.equals(downStation)){
            throw new HttpBadRequestException(String.format("상행역과 하행역은 같을 수 없습니다."));
        }
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
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

    public Line getLine() {
        return line;
    }

    public Long getId() {
        return id;
    }

    public boolean contains(Station downStation) {
        return this.upStation.equals(downStation) || this.downStation.equals(downStation);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
