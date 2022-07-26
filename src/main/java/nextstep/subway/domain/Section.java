package nextstep.subway.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
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
        this(null, line, upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public boolean isEqualToUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isEqualToDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public void changeSectionConditionBy(Section newSection) {
        checkToNewSectionDistance(newSection.getDistance());

        if (isEqualToUpStation(newSection.getUpStation())) {
            changeUpStation(newSection.getDownStation());
        }

        if (isEqualToDownStation(newSection.getDownStation())) {
            changeDownStation(newSection.getUpStation());
        }

        decreaseDistance(newSection.getDistance());
    }

    private void decreaseDistance(Integer distance) {
        this.distance = this.distance - distance;
    }

    private void checkToNewSectionDistance(Integer newDistance) {
        if (!isOriginalDistanceLongThenNew(newDistance)) {
            throw new IllegalArgumentException("역 사이에 등록할 경우 기존의 거리보다 짧은 구간만 등록이 가능합니다.");
        }
    }

    private boolean isOriginalDistanceLongThenNew(Integer newDistance) {
        return newDistance < this.distance;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void addDistance(int distance) {
        this.distance += distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }

}