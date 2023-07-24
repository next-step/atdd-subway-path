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

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean isInMiddle(Section section){
        return this.getUpStation().equals(section.getUpStation());
    }

    public void subtractDistance(Section section){
        if(this.distance <= section.getDistance()) throw new IllegalArgumentException("지하철 노선에 구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다");
        this.distance = this.distance - section.getDistance();
    }

    public boolean isContainStation(Station upStation, Station downStation){
        if(this.upStation.equals(upStation) && this.downStation.equals(downStation)) throw new IllegalArgumentException("지하철 노선에 구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없습니다");
        return this.upStation.equals(upStation)
                || this.upStation.equals(downStation)
                || this.downStation.equals(upStation)
                || this.downStation.equals(downStation);
    }

    public int getDistance() {
        return distance;
    }
}