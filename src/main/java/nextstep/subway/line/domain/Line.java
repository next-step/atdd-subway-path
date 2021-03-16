package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.InvalidStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

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
        sections.add(new Section(this, upStation, downStation, distance));
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

    public void addSection(Station upStation, Station downStation, int distance) {
        final Section newSection = new Section(this, upStation, downStation, distance);
        if (sections.getSectionSize() == 0) {
            sections.add(newSection);
            return;
        }

        validateStations(upStation, downStation);
        Optional<Section> optionalUpStationSection = sections.findSectionByUpStation(upStation);
        if (optionalUpStationSection.isPresent()){
            addUpfrontSection(optionalUpStationSection.get(), newSection);
            return;
        }

        Optional<Section> optionalDownStationSection = sections.findSectionByDownStation(downStation);
        if (optionalDownStationSection.isPresent()){
            addDownBehindSection(optionalDownStationSection.get(), newSection);
            return;
        }
        sections.add(newSection);
    }

    private void addUpfrontSection(Section oldSection, Section newSection) {
        sections.remove(oldSection);
        sections.add(newSection);
        sections.add(new Section(this, newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void addDownBehindSection(Section oldSection, Section newSection) {
        sections.remove(oldSection);
        sections.add(newSection);
        sections.add(new Section(this, oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void validateStations(Station upStation, Station downStation) {
        final boolean existUpStation = isInStation(upStation);
        final boolean existDownStation = isInStation(downStation);
        if (existUpStation && existDownStation) {
            throw new InvalidStationException("이미 두 역은 등록되어 있습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new InvalidStationException("이미 두 역 중 한 역은 등록되어 있어야 합니다.");
        }
    }

    public void removeSection(Station station) {
       sections.remove(station);
    }

    public int getSectionSize(){
        return sections.getSectionSize();
    }

    public List<Station> getStations(){
        return sections.getStations();
    }

    public boolean isInStation(Station station) {
        return sections.getStations().stream().anyMatch(it -> it == station);
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
