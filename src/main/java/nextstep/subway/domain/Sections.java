package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionFailException;
import nextstep.subway.exception.RemoveSectionFailException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    private static final int MIN_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateSection(newSection);

        if (isFirstStation(newSection.getDownStation())) {
            addToFirstSection(newSection);
            return;
        }

        if (isLastStation(newSection.getUpStation())) {
            addToLastSection(newSection);
            return;
        }

        if (hasSameUpStation(newSection.getUpStation())) {
            addToBetweenSections(newSection);
            return;
        }

        throw new AddSectionFailException("역이 존재하지 않음");
    }

    private void validateSection(Section newSection) {
        if (containsStation(newSection.getUpStation()) && containsStation(newSection.getDownStation())) {
            throw new AddSectionFailException("이미 존재하는 역");
        }
    }

    private boolean containsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(station) || section.isDownStation(station));
    }

    private boolean isLastStation(Station station) {
        return getLastSection().isDownStation(station);
    }

    private void addToLastSection(Section newSection) {
        sections.add(newSection);
    }

    private boolean isFirstStation(Station station) {
        return sections.get(0)
                .isUpStation(station);
    }

    private void addToFirstSection(Section newSection) {
        sections.add(0, newSection);
    }

    private boolean hasSameUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(station));
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

    public void removeSection(Station station) {
        validateSectionCount();

        if (isFirstStation(station)) {
            sections.remove(0);
            return;
        }

        if (isLastStation(station)) {
            sections.remove(getLastIndex());
            return;
        }

        if (isUpStation(station)) {
            removeMiddleStation(station);
            return;
        }

        throw new RemoveSectionFailException();
    }

    private void removeMiddleStation(Station station) {
        Section targetSection = findSection(station);
        Section beforeSection = sections.get(sections.indexOf(targetSection) - 1);
        beforeSection.changeDownStation(targetSection.getDownStation(), targetSection.getDistance());
        sections.remove(targetSection);
    }

    private boolean isUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(station));
    }

    private Section findSection(Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseThrow(RemoveSectionFailException::new);
    }

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private void validateSectionCount() {
        if (sections.size() == MIN_SECTIONS_SIZE) {
            throw new RemoveSectionFailException();
        }
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }
}
