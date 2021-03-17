package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
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
    private Sections sections;


    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.sections = new Sections();

        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
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
        return sections.getSections();
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

    public List<Station> getStations() {
        return this.sections.getStations();
    }


    public void addSection(Station upStation, Station downStation, int distance) {

        if (getStations().size() == 0) {
            addSectionInLast(upStation, downStation, distance);
            return;
        }

        //둘다 등록된 역 혹은 둘다 등록되지 않은역은 등록불가
        validateSectionAddable(upStation, downStation, distance);

        if (isFirstStationEqualsWithDownStation(downStation)) {
            addSectionInFirst(upStation, downStation, distance);
            return;
        }

        if (isLastDownStationContains(upStation)) {
            addSectionInLast(upStation, downStation, distance);
            return;
        }


        if (isContainsStationInUpStation(upStation)) {
            addSectionInMiddle(upStation, downStation, distance);
            return;
        }
    }
    private boolean isFirstStationEqualsWithDownStation(Station station) {
        return station.equals(this.sections.getFirstStation());
    }

    private void addSectionInFirst(Station upStation, Station downStation, int distance) {
        this.sections.addSectionInFirst(new Section(this, upStation, downStation, distance));
    }

    private void addSectionInLast(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    private void addSectionInMiddle(Station upStation, Station downStation, int distance) {

        this.sections.addInMiddle(new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Station station) {
        if (this.getSections().size() <= 1) {
            throw  new ApplicationException(ApplicationType.LINE_MUST_BE_HAVE_ONE_SECTION_AT_LEAST);
        }

        if (isNotValidUpStation(station)) {
            throw new ApplicationException(ApplicationType.ONLY_DOWN_STATIONS_CAN_BE_DELETED);
        }

        this.sections.removeSection(station);
    }

    private boolean isNotValidUpStation(Station station){
        return this.sections.isLastDownStationContains(station);
    }

    private boolean isLastDownStationContains(Station station) {
        return this.sections.isLastDownStationContains(station);
    }

    private void validateSectionAddable(Station upStation, Station downStation, int distance) {
        validateContainsBothStations(upStation, downStation);
    }

    private void validateContainsBothStations(Station upStation, Station downStation) {
        if(this.sections.containsBothStation(upStation, downStation)) {
            throw new ApplicationException(ApplicationType.STATIONS_ALREADY_REGISTERD);
        }

        if(this.sections.notContainsBothStation(upStation, downStation)) {
            throw new ApplicationException(ApplicationType.ONE_STATION_MUST_BE_REGISTERED_AT_LEAST);
        }
    }

    private boolean isContainsStationInUpStation(Station station) {
        if (this.sections.getSectionUpStationContains(station) == null) {
            return false;
        }

        return true;
    }

    public int getLineDistance() {
        return this.sections.getToalDistance();
    }
}
