package nextstep.subway.domain;

import static nextstep.subway.exception.ErrorCode.DUPLICATED_SECTION;
import static nextstep.subway.exception.ErrorCode.INVALID_DISTANCE;
import static nextstep.subway.exception.ErrorCode.INVALID_SECTION;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.SubwayException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        if (sections.size() > 1) {
            return sortSectionsByOrder(sections);
        }
        return sections;
    }

    private List<Section> sortSectionsByOrder(List<Section> sections) {
        Station upFirstStation = sections.stream()
            .map(Section::getUpStation)
            .filter(upStation -> sections.stream()
                .noneMatch(section -> section.getDownStation() == upStation))
            .findFirst()
            .orElse(null);

        Section firstSection = sections.stream()
            .filter(section -> section.getUpStation() == upFirstStation)
            .findFirst()
            .orElse(null);

        List<Section> sortedSections = new ArrayList<>();
        Section currentSection = firstSection;
        while (currentSection != null) {
            sortedSections.add(currentSection);
            Station nextUpStation = currentSection.getDownStation();
            currentSection = sections.stream()
                .filter(section -> section.getUpStation() == nextUpStation)
                .findFirst()
                .orElse(null);
        }

        return sortedSections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        // 역 사이에 새로운 역을 등록할 경우
        Section targetSection = sections.stream()
            .filter(s -> section.getUpStation().equals(s.getUpStation()))
            .findFirst()
            .orElse(null);

        if (targetSection != null) {
            if (section.getDistance() >= targetSection.getDistance()) {
                throw new SubwayException(INVALID_DISTANCE);
            }

            if (targetSection.getDownStation() == section.getDownStation()) {
                throw new SubwayException(DUPLICATED_SECTION);
            }
            sections.add(section);
            sections.remove(targetSection);
            Section newSection = new Section(
                targetSection.getLine(),
                section.getDownStation(),
                targetSection.getDownStation(),
                targetSection.getDistance() - section.getDistance());

            sections.add(newSection);
        }

        // 새로운 역을 상행 종점으로 등록할 경우
        boolean match = sections.stream()
            .anyMatch(s -> section.getDownStation().equals(s.getUpStation()));

        if (match) {
            sections.add(section);
        }

        // 새로운 역을 하행 종점으로 등록할 경우
        boolean isMatch = sections.stream()
            .anyMatch(s -> section.getUpStation().equals(s.getDownStation()));

        if (isMatch) {
            sections.add(section);

        }

        boolean isExist = getStations().stream()
            .anyMatch(s -> s.equals(section.getUpStation()) || s.equals(section.getDownStation()));
        if (!isExist) {
            throw new SubwayException(INVALID_SECTION);
        }
    }

    public void deleteSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int lastSectionIndex = sections.size() - 1;
        Station lastSectionDownStation = sections.get(lastSectionIndex).getDownStation();

        if (!lastSectionDownStation.equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(lastSectionIndex);
    }

    public List<Station> getStations() {
        return sections
            .stream()
            .flatMap(section ->
                Stream.of(section.getUpStation(), section.getDownStation())
            )
            .distinct()
            .collect(Collectors.toList());
    }
}
