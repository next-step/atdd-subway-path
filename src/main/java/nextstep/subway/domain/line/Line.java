package nextstep.subway.domain.line;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.Stations;
import nextstep.subway.exception.advice.ValidationException;

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return new Sections(sections);
    }

    public Section getLastSection() {
        return this.getSections().getList().get(this.getSections().getList().size() - 1);
    }

    public Stations getStations() {
        if (this.sections.size() == 0) {
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
            throw new ValidationException("상행역 또는 하행역에 대한 구간이 존재하지 않습니다.");
        }
    }

    private void validateSectionIsAlreadyExists(Section section) {
        if (this.sections.getList().stream()
                .anyMatch(it -> it.getUpStation().equals(section.getUpStation()) &&
                        it.getDownStation().equals(section.getDownStation()))) {
            throw new ValidationException("이미 등록되어있는 구간입니다.");
        }
    }

    private void validateSectionDistance(Section existsSection, Section newSection) {
        if (existsSection.getDistance() <= newSection.getDistance()) {
            throw new ValidationException("기존의 구간의 길이보다 새로운 구간의 길이가 같거나 클 수 없습니다.");
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
            throw new ValidationException("노선의 마지막 구간은 삭제할 수 없습니다");
        }
        if (!this.sections.getLastSection().getDownStation().equals(station)) {
            throw new ValidationException("삭제하려는 역이 하행종점역이 아닙니다.");
        }
    }
}
