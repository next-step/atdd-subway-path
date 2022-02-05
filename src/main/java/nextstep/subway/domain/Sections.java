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

    public void validateNew(Long upStationId, Long downStationId) {
        if (!sections.isEmpty() &&
                doesNotMatchLastDownStation(upStationId) ||
                doesContains(downStationId)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean doesNotMatchLastDownStation(Long upStationId) {
        return !upStationId.equals(getLastDownStation().getId());
    }

    private Station getLastDownStation() {
        return sections.stream()
                .filter(ds -> sections.stream().noneMatch(us ->
                        us.getUpStation().equals(ds.getDownStation())))
                .collect(Collectors.toList())
                .get(0)
                .getDownStation();
    }

    private boolean doesContains(Long downStationId) {
        return sections.stream().anyMatch(s -> s.doesContains(downStationId));
    }

    public void add(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException();
        }
        sections.add(section);
    }

    public List<Station> getSortedStations() {
        sorted();

        List<Station> stations = new ArrayList<>();
        stations.add(getFirstUpStation());
        sections.forEach(s -> stations.add(s.getDownStation()));

        return stations;
    }

    private Station getFirstUpStation() {
        return sections.get(FIRST_SECTION_INDEX).getUpStation();
    }

    private void sorted() {
        Section firstSection = getFirstSection();
        sections.remove(firstSection);
        sections.add(FIRST_SECTION_INDEX, firstSection);

        for (int i = 0; i < sections.size() - 1; i++) {
            findNextSection(i);
        }
    }

    private Section getFirstSection() {
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

    public void remove(Long stationId) {
        Section section = findSection(stationId);
        validatePossibleToRemove(section);
        sections.remove(section);
    }

    private Section findSection(Long stationId) {
        return sections.stream()
                .filter(s -> s.getDownStation().getId().equals(stationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validatePossibleToRemove(Section section) {
        if (sections.size() <= 1 ||
                !section.isLastStation(getLastDownStation())) {
            throw new IllegalArgumentException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
