package nextstep.subway.line;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;
    private Integer distance;

    @ManyToOne
    private Line line;

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }


    public Section() {
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }



    public List<Section> divide(Section newSection) {
        Section next = new Section(newSection.getDownStation(), this.downStation, this.distance - newSection.distance, line);
        this.downStation = newSection.getDownStation();
        return new LinkedList<>(List.of(this, next));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }

    public boolean equalSection(Section newSection) {
        return Objects.equals(upStation, newSection.getUpStation())
                && Objects.equals(downStation, newSection.getDownStation());
    }
}
