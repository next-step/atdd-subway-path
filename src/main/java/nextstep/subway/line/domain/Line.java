package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getAllSections();
    }

    public List<Station> getAllStations() {
        return sections.getAllStations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getAllStations().size() == 0) {
            sections.addSection(new Section(this, upStation, downStation, distance));
            return;
        }

        boolean isNotValidUpStation = sections.getLastSection().getDownStation() != upStation;
        if (isNotValidUpStation) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }

        boolean isDownStationExisted = this.getAllStations().stream().anyMatch(it -> it == downStation);
        if (isDownStationExisted) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }

        sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public Station getLastStation(){
        return sections.getLastSection().getDownStation();
    }

    public void removeSection(Long stationId) {
        if (this.getSections().size() <= 1) {
            throw new RuntimeException("하나의 구간만 존재하여 삭제가 불가능합니다.");
        }

        boolean isNotValidUpStation = this.getLastStation().getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        sections.deleteLastSection(stationId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

}
