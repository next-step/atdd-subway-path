package nextstep.subway.domain;

import nextstep.subway.exception.RemoveSectionFailException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTIONS_SIZE = 1;
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        if (canAddToBetweenSections(newSection)) {
            addToBetweenSections(newSection);
            return;
        }

        if (canAddToFirstSection(newSection)) {
            addToFirstSection(newSection);
            return;
        }

        if (canAddToLastSection(newSection)) {
            addToLastSection(newSection);
            return;
        }

        throw new IllegalArgumentException("역을 추가할 수 없음");
    }

    private void addToLastSection(Section newSection) {
        sections.add(newSection);
    }

    private boolean canAddToLastSection(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isDownStation(newSection.getUpStation()));
    }

    private void addToFirstSection(Section newSection) {
        sections.add(0, newSection);
    }

    private boolean canAddToFirstSection(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(newSection.getDownStation()));
    }

    private boolean canAddToBetweenSections(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(newSection.getUpStation()));
    }

    private void addToBetweenSections(Section newSection) {
        Section targetSection = sections.stream()
                .filter(section -> section.isUpStation(newSection.getUpStation()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        sections.add(sections.indexOf(targetSection), newSection);
        targetSection.changeUpStation(newSection.getDownStation(), newSection.getDistance());
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = findFirstSection();

        stations.add(firstSection.getUpStation());
        stations.add(firstSection.getDownStation());

        Station downStation = firstSection.getDownStation();

        while (sections.size() >= stations.size()) {
            downStation = findNextStation(downStation);
            stations.add(downStation);
        }

        return stations;
    }

    private Station findNextStation(Station downStation) {
        return sections.stream()
                .filter(s -> s.isUpStation(downStation))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getDownStation();
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(s1 -> sections.stream()
                        .noneMatch(s2 -> s2.getDownStation().equals(s1.getUpStation())))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public void removeSection(Long stationId) {
        validateSectionCount();
        validateIsLastStation(stationId);
        sections.remove(getLastSection());
    }

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private void validateIsLastStation(Long stationId) {
        if (!isLastStation(stationId)) {
            throw new RemoveSectionFailException();
        }
    }

    private void validateSectionCount() {
        if (sections.size() == MIN_SECTIONS_SIZE) {
            throw new RemoveSectionFailException();
        }
    }

    private boolean isLastStation(Long stationId) {
        return getLastDownStation().getId()
                .equals(stationId);
    }

    private Station getLastDownStation() {
        return sections.get(getLastIndex())
                .getDownStation();
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }
}
