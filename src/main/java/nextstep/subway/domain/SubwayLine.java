package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwayLine extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    private Long distance = 0L;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private Sections sections = new Sections();

    protected SubwayLine(String name, String color) {
        super();
        this.name = name;
        this.color = color;
    }

    protected void addFirstSection(Section section) {
        this.upStation = section.getUpStation();
        addSection(section);
    }

    public void addSection(Section section) {
        this.distance = this.distance + section.getDistance();
        this.downStation = section.getDownStation();
        this.sections.addSection(section);
        section.assignSubwayLine(this);
    }

    public void updateBasicInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void removeSection(Long stationId) {
        var removedSection = sections.removeSection(stationId);
        this.downStation = removedSection.getUpStation();
        distance -= removedSection.getDistance();
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
