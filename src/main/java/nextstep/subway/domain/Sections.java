package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void removeLast() {
        sections.remove(sections.size() - 1);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void add(final Section section) {
        sections.add(section);
    }

    public void add(final int index, final Section section) {
        sections.add(index, section);
    }

    public List<Station> findAllStationsInOrder() {
        final List<Station> stationList = new ArrayList<>();
        final List<Section> sectionList = getSections();

        Station target = getFirstUpStation();
        while (target != null) {
            stationList.add(target);
            target = findConnectedDownStation(sectionList, target);
        }

        return Collections.unmodifiableList(stationList);
    }

    private Station getFirstUpStation() {
        final List<Station> allDownStations = getAllDownStations();
        return sections.stream()
                .filter(v -> !allDownStations.contains(v.getUpStation()))
                .findFirst()
                .map(Section::getUpStation)
                .orElseThrow(IllegalStateException::new);
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private Station findConnectedDownStation(final List<Section> sectionList, final Station target) {
        return sectionList.stream()
                .filter(v -> v.getUpStation().equals(target))
                .map(Section::getDownStation)
                .findAny()
                .orElse(null);
    }

    public void remove(final Station station) {
        final List<Station> stations = findAllStationsInOrder();
        final Section section = sections.stream()
                .filter(v -> v.getUpStation().equals(station) || v.getDownStation().equals(station))
                .findAny()
                .orElseThrow(NoSuchElementException::new);

        // 1, 2 -> 2, 3 -> 3, 4 -> 4, 5
        if (stations.get(0).equals(station) || stations.get(stations.size() - 1).equals(station)) {
            sections.remove(section);
        } else {
            final Section afterSection = sections.get(sections.indexOf(section) + 1);
            section.updateDownStationAndDistance(afterSection.getDownStation(), section.getDistance() + afterSection.getDistance());
            sections.remove(afterSection);
        }

    }
}
