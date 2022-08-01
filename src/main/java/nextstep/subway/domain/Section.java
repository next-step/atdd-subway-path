package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
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
    }

    public boolean isSameUpDownStation(Section newSection) {
        return upStation.equals(newSection.getUpStation()) || downStation.equals(newSection.getDownStation());
    }

    public void minusDistance(int newDistance) {
        this.distance -= newDistance;
    }

    public List<Station> getRelatedStation() {
        return List.of(upStation, downStation);
    }

    public void modifyBetweenSection(Section newSection) {
        if(this.upStation.equals(newSection.getUpStation())){
            this.upStation = newSection.getDownStation();
        }
        if(this.downStation.equals(newSection.getDownStation())){
            this.downStation = newSection.getUpStation();
        }
    }
}