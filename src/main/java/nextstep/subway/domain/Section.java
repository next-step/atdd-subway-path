package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

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

    public void updateUpStation(Station station, int distance) {
        this.upStation = station;
        this.distance = distance;
    }

    public void updateDownStation(Station station, int distance) {
        this.downStation = station;
        this.distance = distance;
    }

    /**
     * 해당 구간이 상행종점역을 포함하는 구간인지 여부 확인
     * @param sections 구간목록
     */
    public boolean isSectionWithLastUpStation(List<Section> sections) {
        return sections.stream()
                .noneMatch((section -> section.getDownStation().equals(upStation)));
    }

    /**
     * 하행종점역을 포함하는 구간인지 여부 확인
     * @param sections 구간목록
     */
    public boolean isSectionWithLastDownStation(List<Section> sections) {
        return sections.stream()
                .noneMatch((section -> section.getUpStation().equals(downStation)));
    }

    /**
     * 상행역·하행역에 매칭되는 구간인지 여부 확인
     * @param upStation 상행역
     * @param downStation 하행역
     */
    public boolean matchStations(Station upStation, Station downStation) {
        return this.getUpStation().equals(upStation)
                && this.getDownStation().equals(downStation);
    }

    /**
     * 지하철역이 해당 구간의 상행역과 일치하는지 여부 확인
     * @param station 지하철역
     */
    public boolean matchUpStation(Station station) {
        return upStation.equals(station);
    }

    /**
     * 지하철역이 해당 구간의 하행역과 일치하는지 여부 확인
     * @param station 지하철역
     */
    public boolean matchDownStation(Station station) {
        return downStation.equals(station);
    }

}