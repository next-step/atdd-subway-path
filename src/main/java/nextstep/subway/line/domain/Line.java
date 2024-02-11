package nextstep.subway.line.domain;

import nextstep.subway.exception.AlreadyExistDownStationException;
import nextstep.subway.exception.DeleteSectionException;
import nextstep.subway.exception.IsNotLastStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE line SET deleted_at = CURRENT_TIMESTAMP where line_id = ?")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20)
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @Column
    private Integer distance;

    @Column
    private Timestamp deleted_at;

    protected Line() {
    }

    private Line(String name, String color, Integer distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public static Line from(String name, String color, Integer distance) {
        return new Line(name, color, distance);
    }

    public void updateLine(String color, Integer distance) {
        this.color = color;
        this.distance = distance;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : this.sections) {
            stations.add(section.getUpStation());
        }

        stations.add(this.sections.get(this.sections.size() - 1).getDownStation());

        return stations;
    }

    public void registerSection(Section section) {
        this.sections.add(section);
        section.setLine(this);
    }

    public void addSection(Section addedSection) {
        if (isLastStation(addedSection.getUpStation())) {
            throw new IsNotLastStationException();
        }
        if (isExistDownStation(addedSection)) {
            throw new AlreadyExistDownStationException();
        }
        registerSection(addedSection);
    }

    public void deleteSection(Station deletedStation) {
        if (isLastStation(deletedStation)) {
            throw new IsNotLastStationException();
        }
        if (sections.size() == 1) {
            throw new DeleteSectionException();
        }
        this.sections.get(getLastSectionIndex()).delete();
        this.sections.remove(getLastSectionIndex());
    }

    private boolean isExistDownStation(Section section) {
        return getStations().stream()
                .anyMatch(comparedStation ->
                        comparedStation.equals(section.getDownStation())
                );
    }

    private boolean isLastStation(Station station) {
        return !this.sections.get(getLastSectionIndex()).equalsLastStation(station);
    }

    private int getLastSectionIndex() {
        return this.sections.size() - 1;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Line line = (Line) o;
        return Objects.equals(lineId, line.getLineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId);
    }

}
