package nextstep.subway.domain;

import nextstep.subway.error.exception.InvalidValueException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
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
        throw new InvalidValueException(section.getId());
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
        List<Section> sortedSections = getSorted();
        List<Station> stations = new ArrayList<>();
        stations.add(getFirstUpStationWhenSorted(sortedSections));
        sortedSections.forEach(s -> stations.add(s.getDownStation()));

        return stations;
    }

    private Station getFirstUpStationWhenSorted(List<Section> sortedSections) {
        return sortedSections.get(FIRST_SECTION_INDEX).getUpStation();
    }

    private List<Section> getSorted() {
        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(findFirstSection());
        do {
            addNextSections(sortedSections);
        } while (sortedSections.size() < sections.size());
        return sortedSections;
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(us ->
                        sections.stream().noneMatch(ds ->
                                ds.getDownStation().equals(us.getUpStation())))
                .collect(Collectors.toList())
                .get(0);
    }

    private void addNextSections(List<Section> sortedSections) {
        for (int i = 0; i < sections.size(); i++) {
            findNextSection(sortedSections, sections.get(i));
        }
    }

    private void findNextSection(List<Section> sortedSections, Section section) {
        Section lastSection = sortedSections.get(sortedSections.size() - 1);
        if (lastSection.equals(section)) {
            return;
        }
        if (isNextSection(lastSection, section)) {
            sortedSections.add(section);
        }
    }

    private boolean isNextSection(Section lastSection, Section section) {
        return lastSection.compareTo(section) > 0;
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
                .orElseThrow(() -> {
                    throw new InvalidValueException(station.getId());
                });
    }

    private void validatePossibleToRemove(Section section) {
        if (sections.size() <= 1 ||
                !section.isLastStation(findLastDownStation())) {
            throw new InvalidValueException(section.getId());
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
