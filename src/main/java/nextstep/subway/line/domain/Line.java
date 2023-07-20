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

    private String firstStationName;

    private String lastStationName;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.firstStationName = section.getUpStationName();
        this.lastStationName = section.getDownStationName();

        sections.add(section);
        section.assignLine(this);
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

    public String getFirstStationName() {
        return firstStationName;
    }

    public String getLastStationName() {
        return lastStationName;
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

    public void addSectionVer2(Section newSection) {
        // 새로운 구간의 상, 하행역이 모두 같은 구간이 이미 존재하는 경우 예외
        sections.stream()
                .filter(section -> section.hasAllSameStations(newSection))
                .findAny()
                .ifPresent(section -> {
                    throw new AlreadyRegisteredStationException();
                });

        if (newSection.downStationNameEqualsTo(firstStationName)) {
            firstStationName = newSection.getUpStationName();
            sections.add(newSection);
            newSection.assignLine(this);
            return;
        }

        if (newSection.upStationNameEqualsTo(lastStationName)) {
            lastStationName = newSection.getDownStationName();
            sections.add(newSection);
            newSection.assignLine(this);
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


        Section downSection;
        if (existingSection.hasSameUpStation(newSection)) {
            downSection = new Section(newSection.getDownStation(), existingSection.getDownStation(), existingSection.getDistance() - newSection.getDistance());
        } else {
            downSection = new Section(existingSection.getUpStation(), newSection.getUpStation(), existingSection.getDistance() - newSection.getDistance());
        }

        sections.remove(existingSection);

        sections.add(newSection);
        sections.add(downSection);

        newSection.assignLine(this);
        downSection.assignLine(this);
    }
}
