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

    public boolean isUpstation(Long stationId) {
        return Objects.equals(upStation.getId(), stationId);
    }

    public boolean equalsDownStation(Long stationId) {
        return Objects.equals(downStation.getId(), stationId);
    }
}
