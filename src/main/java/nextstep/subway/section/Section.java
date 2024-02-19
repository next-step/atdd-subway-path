package nextstep.subway.section;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Long nextSectionId;

    public Section() {}

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance, Long nextSectionId) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.nextSectionId = nextSectionId;
    }

    public Section(Long id, Station upStation, Station downStation, int distance, Long nextSectionId) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.nextSectionId = nextSectionId;
    }

    public Long getId() {
        return id;
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

    public Line getLine() {
        return line;
    }

    public Long getNextSectionId() {
        return nextSectionId;
    }

    public void changeNextSection(Section section) {
        this.nextSectionId = section.getId();
    }

    public void registerLine(Line line) {
        this.line = line;
        line.getSectionList().add(this);
    }

    public void validMiddleSection(Section otherSection) {
        if(upStation.equals(otherSection.getUpStation()) && downStation.equals(otherSection.downStation)){
            throw new BadRequestException("기존 구간과 같은 구간은 추가할 수 없습니다.");
        }
        if(distance >= otherSection.getDistance()) {
            throw new BadRequestException("기존 구간보다 긴 구간은 추가할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(getLine(), section.getLine()) && Objects.equals(nextSectionId, section.nextSectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUpStation(), getDownStation(), getDistance(), getLine(), nextSectionId);
    }
}
