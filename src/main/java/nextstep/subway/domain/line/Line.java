package nextstep.subway.domain.line;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.Stations;
import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public Sections getSections() {
        return sections;
    }

    public Stations getStations() {
        if (this.sections.getList().isEmpty()) {
            return new Stations(Collections.emptyList());
        }

        // Todo: upStationSection 변수 final 로 리팩터링
        // upStationSection 을 final 로 정의하고 싶은데 재귀함수를 구현하는데에 시간이 너무 많이 소요되어
        // 해당 부분은 우선 final 을 쓰지 않는 방향으로 작성하겠습니다..
        final Stations stations = new Stations(new ArrayList<>());
        Section upStationSection = this.sections.getUpStationSection();
        stations.add(upStationSection);

        while (true) {
            Section nextSection = getNextSection(upStationSection);
            if (nextSection == null) {
                break;
            }
            stations.add(nextSection.getDownStation());
            upStationSection = nextSection;
        }
        return stations;
    }

    public void addSection(Section section) {
        if (this.sections.getList().isEmpty()) {
            this.sections.add(section);
            return;
        }

        validateUpStationAndDownStationInSection(section.getUpStation(), section.getDownStation());
        validateSectionIsAlreadyExists(section);

        // 특정 구간 다음으로 추가해야하는지 검색
        final Section previousSection = this.getPreviousSection(section);

        // 역과 역 사이에 구간할 시, 이미 존재하는 구간의 upStation 을 추가하려는 구간의 downStation 을 바라보도록 변경
        if (previousSection != null && previousSection.getUpStation().equals(section.getUpStation())) {
            updatePreviousSectionBeforeAddNewSection(previousSection, section);
        }
        this.sections.add(section);
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public void removeSection(Station station) {
        validateBeforeRemoveSection(station);
        this.sections.remove(this.sections.getLastSection());
    }

    private Section getPreviousSection(Section newSection) {
        return this.sections.getList().stream()
                .filter(it -> it.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    private void updatePreviousSectionBeforeAddNewSection(Section previousSection, Section newSection) {
        validateSectionDistance(previousSection, newSection);
        previousSection.update(
                newSection.getDownStation(),
                null,
                previousSection.getDistance() - newSection.getDistance()
        );
    }

    private void validateUpStationAndDownStationInSection(Station upStation, Station downStation) {
        final Stations stations = this.getStations();
        if (stations.getList().stream()
                .noneMatch(it -> it.getName().equals(upStation.getName()) ||
                        it.getName().equals(downStation.getName()))) {
            throw new BusinessException(ErrorCode.SECTION_NOT_FOUND_ABOUT_UP_AND_DOWN_STATION);
        }
    }

    private void validateSectionIsAlreadyExists(Section section) {
        if (this.sections.getList().stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()) &&
                        it.getDownStation().equals(section.getDownStation()))) {
            throw new BusinessException(ErrorCode.SECTION_ALREADY_EXISTS);
        }
    }

    private void validateSectionDistance(Section existsSection, Section newSection) {
        if (existsSection.getDistance() <= newSection.getDistance()) {
            throw new BusinessException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
    }

    private Section getNextSection(Section section) {
        return this.sections.getList().stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    private void validateBeforeRemoveSection(Station station) {
        if (this.getSections().getList().size() < 2) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_SECTION_IF_IS_NOT_DOWN_STATION);
        }
        if (!this.sections.getLastSection().getDownStation().equals(station)) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_LAST_SECTION);
        }
    }
}
