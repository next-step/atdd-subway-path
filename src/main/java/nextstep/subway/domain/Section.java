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

    public void addDistance(int distance){ this.distance += distance; }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public boolean equalsUpStation(Section section) {
        return this.getUpStation().getId().equals(section.getUpStation().getId());
    }

    public boolean equalsUpStation(Station station) {
        return this.getUpStation().getId().equals(station.getId());
    }

    public boolean equalsDownStation(Section section) {
        return this.getDownStation().getId().equals(section.getDownStation().getId());
    }

    public boolean equalsDownStation(Station station) {
        return this.getDownStation().getId().equals(station.getId());
    }

    public boolean equalsUpOrDownStation(Section newSection){
        return this.equalsUpStation(newSection) || this.equalsDownStation(newSection);
    }

    public boolean isLessThan(Section newSection){
        return this.getDistance() <= newSection.getDistance();
    }

    public void divideSection(Section newSection){
        if(this.equalsUpStation(newSection)){
            this.reduceDistance(newSection.getDistance());
            this.updateUpStation(newSection.getDownStation());
        }else if(this.equalsDownStation(newSection)){
            this.reduceDistance(newSection.getDistance());
            this.updateDownStation(newSection.getUpStation());
        }
    }
}