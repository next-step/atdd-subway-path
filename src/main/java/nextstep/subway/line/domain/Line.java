package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.*;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.AlreadyRegisteredStationException;
import nextstep.subway.section.exception.CanNotDeleteOnlyOneSectionException;
import nextstep.subway.section.exception.DeleteOnlyTerminusStationException;
import nextstep.subway.section.exception.InvalidSectionRegistrationException;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @OneToOne(fetch = FetchType.LAZY)
    private Section firstSection;

    @OneToOne(fetch = FetchType.LAZY)
    private Section lastSection;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.firstSection = section;
        this.lastSection = section;

        sections.add(section);
        section.assignLine(this);
    }

    public void addSection(Section newSection) {
        Section lastSection = getLastSectionVer0();
        validateLastStationEqualToNewUpStation(lastSection, newSection);
        validateDuplicationOfStationInLine(newSection);

        sections.add(newSection);
        newSection.assignLine(this);
    }

    private void validateLastStationEqualToNewUpStation(Section lastSection, Section newSection) {
        if (!lastSection.downStationEqualsToUpStationOf(newSection)) {
            throw new InvalidSectionRegistrationException();
        }
    }

    private void validateDuplicationOfStationInLine(Section newSection) {
        if (hasStation(newSection.getDownStation())) {
            throw new AlreadyRegisteredStationException();
        }
    }

    public void update(String name, String color) {
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

    public List<Section> getSections() {
        return sections;
    }

    public Section getFirstSection() {
        return firstSection;
    }

    public Section getLastSection() {
        return lastSection;
    }

    public boolean hasStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.upStationEqualsTo(downStation));
    }

    public void deleteSection(Station station) {
        validateLineHasOnlyOneSection();
        validateStationIsDownStationOfLastSection(station);

        sections.remove(getLastSectionVer0());
    }

    private void validateLineHasOnlyOneSection() {
        if (hasOnlyOneSection()) {
            throw new CanNotDeleteOnlyOneSectionException();
        }
    }

    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    private void validateStationIsDownStationOfLastSection(Station station) {
        if (!getLastSectionVer0().downStationEqualsTo(station)) {
            throw new DeleteOnlyTerminusStationException();
        }
    }

    private Section getLastSectionVer0() {
        sections.sort(Comparator.comparing(Section::getId));
        return sections.get(sections.size() - 1);
    }

    /**
     * 1. newSection이 들어갈 수 있는 조건이 있는지 확인한다.
     *  - 상행 종점에 등록되는 새로운 역인가? -> 상행 종점 정보 필요
     *  - 하행 종점에 등록되는 새로운 역인가? -> 하행 종점 정보 필요
     *  - 역 사이에 등록되는 새로운 역인가? -> 위 두 케이스가 아닐 경우, newSection의 상행역 또는 하행역과 같은 상행역 또는 하행역을 갖는 구간 서치 필요
     * 2. 조건에 맞는 구간 등록을 수행한다.
     */
    public void addSectionVer2(Section newSection) {
        // 새로운 구간의 상, 하행역이 모두 같은 구간이 이미 존재하는 경우 예외
        sections.stream()
                .filter(section -> section.hasAllSameStations(newSection))
                .findAny()
                .ifPresent(section -> {
                    throw new AlreadyRegisteredStationException();
                });

        // 상행 종점에 등록
        // 상행 종점의 상행역과 새로운 구간의 하행역이 같은지?
        if (firstSection.upStationEqualsTo(newSection.getDownStation())) {
            // 같다면 상행 종점을 새로운 구간으로 교체
            firstSection = newSection;
            sections.add(newSection);
            return;
        }

        // 하행 종점에 등록
        if (lastSection.downStationEqualsTo(newSection.getUpStation())) {
            lastSection = newSection;
            sections.add(newSection);
            return;
        }

        addSectionBetweenStations(newSection);
    }

    private void addSectionBetweenStations(Section newSection) {
        Section existingSection = sections.stream()
                .filter(section -> section.hasOnlyOneSameStation(newSection))
                .findAny()
                .orElseThrow(InvalidSectionRegistrationException::new);

        if (existingSection.hasSameDistance(newSection)) {
            throw new InvalidSectionRegistrationException();  //TODO: 더 알맞는 예외는 나중에...
        }

        sections.add(newSection);
        sections.remove(existingSection);

        Section downSection;
        if (existingSection.hasSameUpStation(newSection)) {
            downSection = new Section(newSection.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - newSection.getDistance());
        } else {
            downSection = new Section(existingSection.getUpStation(), newSection.getUpStation(), existingSection.getDistance() - newSection.getDistance());
        }

        sections.add(downSection);
    }
}
