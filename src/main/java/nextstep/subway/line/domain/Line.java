package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;

        sections.add(section);
        section.assignLine(this);
    }

    public void addSection(Section newSection) {
        Section lastSection = getLastSection();
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

    public boolean hasStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(downStation));
    }

    public void deleteSection(Station station) {
        validateLineHasOnlyOneSection();
        validateStationIsDownStationOfLastSection(station);

        sections.remove(getLastSection());
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
        if (!getLastSection().downStationEqualsTo(station)) {
            throw new DeleteOnlyTerminusStationException();
        }
    }

    private Section getLastSection() {
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

        // 1. newStation의 상행역 또는 하행역을 갖는 구간을 서치
        Section existingSection = sections.stream()
                .filter(section -> section.hasOnlyOneSameStation(newSection))
                .findAny()
                .orElseThrow(InvalidSectionRegistrationException::new);

        // 1-2. 거리가 같으면 예외
        if (existingSection.hasSameDistance(newSection)) {
            throw new InvalidSectionRegistrationException();  //TODO: 더 알맞는 예외는 나중에...
        }

        // 2. 상행역이 같은지, 하행역이 같은지 확인

        // 새로운 섹션을 추가
        sections.add(newSection);

        // 기존 구간을 제거
        sections.remove(existingSection);

        Section downSection;
        if (existingSection.hasSameUpStation(newSection)) {
            // 상행역이 기존 구간과 같은경우
            // 새 구간의 하행을 상행으로, 기존 구간의 하행을 하행으로 갖는 section 추가
            downSection = new Section(newSection.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - newSection.getDistance());
        } else {
            // 하행역이 기존 구간과 같은 경우
            // 기존 구간의 상행을 상행으로, 새 구간의 상행을 하행으로 갖는 section 추가
            downSection = new Section(existingSection.getUpStation(), newSection.getUpStation(), existingSection.getDistance() - newSection.getDistance());
        }

        sections.add(downSection);
    }
}
