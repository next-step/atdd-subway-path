package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public boolean containStation(Long stationId) {
        return Objects.equals(upStation.getId(), stationId) || Objects.equals(downStation.getId(), stationId);
    }

    public void attachToLine(Line line) {
        this.line = line;
    }

    public boolean equalsUpstation(Long stationId) {
        return Objects.equals(upStation.getId(), stationId);
    }

    public boolean equalsUpstation(Station station) {
        return Objects.equals(upStation.getId(), station.getId());
    }
    public boolean equalsDownStation(Long stationId) {
        return Objects.equals(downStation.getId(), stationId);
    }

    public boolean equalsDownStation(Station station) {
        return Objects.equals(downStation.getId(), station.getId());
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void decreaseDistance(int newSectionDistance) {
        this.distance -= newSectionDistance;
    }

    public boolean distanceIsLessThanEquals(int distance) {
        return this.distance <=distance;
    }

    public Section mergeSection(Section section) {
        Station newUpStation = this.upStation;
        Station newDownStation = this.downStation;

        if (this.upStation.equals(section.getDownStation())) {
            newUpStation = section.getUpStation();
            newDownStation = this.downStation;
        }

        if (this.downStation.equals(section.getUpStation())) {
            newUpStation = this.upStation;
            newDownStation = section.getDownStation();
        }

        return Section.builder()
                      .upStation(newUpStation)
                      .downStation(newDownStation)
                      .line(line)
                      .distance(this.distance + section.distance)
                      .build();
    }
}
