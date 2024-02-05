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
        if (!line.getSections().isEmpty() && line.getSections().stream().anyMatch(section -> section.contains(downStation))) {
            throw new HttpBadRequestException("이미 등록된 구간입니다.");
        }
        if (!line.getSections().isEmpty() && !Objects.equals(line.getSections().get(line.getSections().size() - 1).getDownStation(), upStation)) {
            throw new HttpBadRequestException(String.format("상행역은 현재 종점역(%s)이어야 합니다.", downStation.getName()));
        }
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

}
