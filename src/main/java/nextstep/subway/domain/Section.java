package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;
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

    public boolean isSameUpStation(final Station newStation) {
        return this.upStation.equals(newStation);
    }

    public boolean isSameDownStation(final Station newStation) {
        return this.downStation.equals(newStation);
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public void addMiddleSection(final Section newSection) {
        distanceValidation(newSection);
        updateReduceDistance(newSection);

        if(this.upStation.equals(newSection.getUpStation())){
            this.upStation = newSection.getDownStation();
        }
        if(this.downStation.equals(newSection.getDownStation())){
            this.downStation = newSection.getUpStation();
        }
    }

    private void distanceValidation(final Section newSection) {
        if (this.distance <= newSection.distance) {
            throw new IllegalStateException("기존의 거리가 새로운 거리보다 작습니다.");
        }
    }

    private void updateReduceDistance(final Section newSection) {
        this.distance -= newSection.distance;
    }

    public void removeMiddleSection(final int backDistance, final Station backStation) {
        this.distance += backDistance;
        this.downStation = backStation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
            section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }


}