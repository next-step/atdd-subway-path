package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_SECTION_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (isValidToEnd(upStation, downStation)) {
            sections.add(section);
            return;
        }
        if (isValidToMiddle(upStation, downStation)) {
            addMiddle(section);
            return;
        }
        throw new IllegalArgumentException();
    }

    public boolean isValidToEnd(Station upStation, Station downStation) {
        if (sections.isEmpty()) {
            return true;
        }

        if (isNotValidToFirst(upStation, downStation) &&
                isNotValidToLast(upStation, downStation)) {
            return false;
        }
        return true;
    }

    private boolean isNotValidToFirst(Station upStation, Station downStation) {
        return doesContains(upStation) ||
                doesNotMatchFirstUpStation(downStation);
    }

    private boolean doesNotMatchFirstUpStation(Station downStation) {
        return !downStation.equals(findFirstUpStation());
    }

    private Station findFirstUpStation() {
        return sections.stream()
                .filter(us -> sections.stream().noneMatch(ds ->
                        ds.getDownStation().equals(us.getUpStation())))
                .collect(Collectors.toList())
                .get(FIRST_SECTION_INDEX)
                .getUpStation();
    }

    private boolean isNotValidToLast(Station upStation, Station downStation) {
        return doesContains(downStation) ||
                doesNotMatchLastDownStation(upStation);
    }

    private boolean doesNotMatchLastDownStation(Station upStation) {
        return !upStation.equals(findLastDownStation());
    }

    private Station findLastDownStation() {
        return sections.stream()
                .filter(ds -> sections.stream().noneMatch(us ->
                        us.getUpStation().equals(ds.getDownStation())))
                .collect(Collectors.toList())
                .get(FIRST_SECTION_INDEX)
                .getDownStation();
    }

    private boolean doesContains(Station station) {
        return sections.stream().anyMatch(s -> s.doesContains(station));
    }

    private boolean isValidToMiddle(Station upStation, Station downStation) {
        return doesContains(upStation) &&
                !doesContains(downStation) &&
                doesNotMatchLastDownStation(upStation);
    }

    private void addMiddle(Section section) {
        Section targetSection = findTargetSection(section.getUpStation());
        targetSection.changeUpStationToNewDownStation(section);
        sections.add(section);
    }

    private Section findTargetSection(Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .collect(Collectors.toList())
                .get(FIRST_SECTION_INDEX);
    }

    public List<Station> getSortedStations() {
        sorted();

        List<Station> stations = new ArrayList<>();
        stations.add(getFirstUpStationWhenSorted());
        sections.forEach(s -> stations.add(s.getDownStation()));

        return stations;
    }

    private Station getFirstUpStationWhenSorted() {
        return sections.get(FIRST_SECTION_INDEX).getUpStation();
    }

    private void sorted() {
        Section firstSection = findFirstSection();
        sections.remove(firstSection);
        sections.add(FIRST_SECTION_INDEX, firstSection);

        for (int i = 0; i < sections.size() - 1; i++) {
            findNextSection(i);
        }
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(us ->
                        sections.stream().noneMatch(ds ->
                                ds.getDownStation().equals(us.getUpStation())))
                .collect(Collectors.toList())
                .get(0);
    }

    private void findNextSection(int i) {
        for (int j = i + 1; j < sections.size(); j++) {
            swap(i, j);
        }
    }

    private void swap(int i, int j) {
        if (isNextSection(i, j)) {
            Collections.swap(sections, i + 1, j);
        }
    }

    private boolean isNextSection(int i, int j) {
        return sections.get(i).compareTo(sections.get(j)) > 0;
    }

    public void remove(Station station) {
        Section section = findSection(station);
        validatePossibleToRemove(section);
        sections.remove(section);
    }

    private Section findSection(Station station) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validatePossibleToRemove(Section section) {
        if (sections.size() <= 1 ||
                !section.isLastStation(findLastDownStation())) {
            throw new IllegalArgumentException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
