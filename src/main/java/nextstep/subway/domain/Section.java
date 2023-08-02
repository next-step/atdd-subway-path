package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    @Getter
    private Line line;
    @Column
    @Getter
    private Long distance;

    @OneToOne
    @JoinColumn(name = "up_station_id")
    @Getter
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id")
    @Getter
    private Station downStation;

    @Builder
    public Section(Long id, Station upStation, Station downStation, Long distance, Line line) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Line line, Long distance, Station upStation, Station downStation) {
        return Section.builder().distance(distance).line(line).upStation(upStation).downStation(downStation).build();
    }

    public boolean containsSameStations(Section otherSection){
        return (upStation.equals(otherSection.getUpStation()) && downStation.equals(otherSection.getDownStation()));
    }
    public boolean containSameUpStation(Section otherSection){
        return (upStation.equals(otherSection.getUpStation()));
    }
    public boolean containSameDownStation(Section otherSection){
        return (downStation.equals(otherSection.getDownStation()));
    }
    public boolean containSameUpStation(Station station){
        return (upStation.equals(station));
    }
    public boolean containSameDownStation(Station Station){
        return (downStation.equals(Station));
    }
    private List<Station> getStations(){
        return Arrays.asList(upStation, downStation);
    }
    public boolean containSameStation(Section otherSection){
        return (getStations().contains(otherSection.getUpStation()) || getStations().contains(otherSection.getDownStation()));
    }
}
