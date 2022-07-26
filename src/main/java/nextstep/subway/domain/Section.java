package nextstep.subway.domain;

import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.SectionException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
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

    @Builder
    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public boolean isEqualsUpStationAndDownStation(Section section) {
        return upStation.equals(section.getUpStation()) && downStation.equals(section.getDownStation());
    }

    private void checkOverLength(int insertDistance) {
        if(distance <= insertDistance) {
            throw new SectionException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    private void minusDistance(int distance) {
        checkOverLength(distance);
        this.distance -= distance;
    }

    public void changeUpStation(Station station, int distance) {
        minusDistance(distance);
        this.upStation = station;
    }

    public void changeDownStation(Station station, int distance) {
        minusDistance(distance);
        this.downStation = station;
    }

    public void removeUpdateStation(Station station, int distance) {
        this.distance += distance;
        this.downStation = station;
    }

    public boolean isConnection(Section section) {
        return isUpStationConnection(section)
            || isDownStationConnection(section);
    }

    public boolean isUpStationConnection(Section section) {
        return upStation.equals(section.getUpStation())
            || upStation.equals(section.getDownStation());
    }

    public boolean isDownStationConnection(Section section) {
        return downStation.equals(section.getUpStation())
            || downStation.equals(section.getDownStation());
    }

}