package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.constants.ErrorMessage;
import nextstep.subway.applicaion.exception.SectionDistanceException;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void modifyDistanceForAdd(int distance){
        if(this.distance <= distance){
            throw new SectionDistanceException(ErrorMessage.LENGTH_ERROR);
        }
        this.distance -= distance;
    }
    public void modifyDistanceForRemove(int distance){
        this.distance += distance;
    }

    public void modifyUp(Station station){
        this.upStation = station;
    }
    public void modifyDown(Station station){
        this.downStation = station;
    }

    public boolean isUp(Station station){
        return upStation.equals(station);
    }

    public boolean isDown(Station station){
        return downStation.equals(station);
    }

    public boolean isExistedStation(Station station){
        return isUp(station) || isDown(station);
    }
}