package subway.domain;

import java.util.Objects;
import java.util.Set;
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

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(nullable = false)
    private Long distance;

    @Builder
    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line)
            && Objects.equals(upStation, section.upStation) && Objects.equals(
            downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation == station;
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation == station;
    }

    public boolean isIncludeStations(Set<Station> stationSet) {
        return stationSet.contains(this.upStation)
            && stationSet.contains(this.downStation);
    }

    public boolean isExcludeStations(Set<Station> stationSet) {
        return (!stationSet.contains(this.upStation)
            && !stationSet.contains(this.downStation));
    }

    public boolean hasLoggerDistance(Section section) {
        return (this.distance >= section.distance);
    }

    public boolean isInsertedBetween(Section section) {
        return section.upStation.equals(this.upStation) ||
            section.downStation.equals(this.downStation);
    }

    public boolean isAppendedToEnds(Section section) {
        return section.upStation.equals(this.downStation)
            || section.downStation.equals(this.upStation);
    }

    public boolean isSameUpStation(Section section) {
        return section.upStation.equals(this.upStation);
    }

    public boolean containsStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }
}
