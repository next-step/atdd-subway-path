package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String color;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Sections sections = new Sections();

    public void changeNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (!sections.possibleToAddSection(section)) {
            throw new SubwayException(ErrorCode.SECTION_ADD_FAIL);
        }

        /**
         * 상행 / 하행 종점역 업데이트
         */
        if (sections.requireUpStationChange(section)) {
            upStation = section.getUpStation();
        }

        if (sections.requireDownStationChange(section)) {
            downStation = section.getDownStation();
        }

        section.attachToLine(this);
        sections.appendSection(section);
    }

    public void deleteSection(Station station) {
        sections.possibleToDeleteSection(station);

        Optional<Station> maybeNextUpStation = Optional.empty();
        Optional<Station> maybeNextDownStation = Optional.empty();

        // 상행 종점역 제거시
        if (equalUpStation(station)) {
            maybeNextUpStation = sections.findSectionByUpStation(station)
                                         .map(Section::getDownStation);
        }

        if (equalDownStation(station)) {
            maybeNextDownStation = sections.findSectionByDownStation(station)
                                           .map(Section::getUpStation);
        }

        sections.deleteSectionByStation(station);

        maybeNextUpStation.ifPresent(nextUpStation -> this.upStation = nextUpStation);
        maybeNextDownStation.ifPresent(nextDownStation -> this.downStation = nextDownStation);
    }

    public boolean equalUpStation(Station station) {
        return Objects.equals(upStation, station);
    }

    public boolean equalDownStation(Station station) {
        return Objects.equals(downStation, station);
    }

    public List<Section> orderedSections() {
        return sections.toOrderedList(upStation);
    }

}
