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
        final Section section = findSection(station);

        // 1, 2 -> 2, 3 -> 3, 4 -> 4, 5
        if (!isFirstOrLastStation(station)) {
            updateMiddleSection(section);
        }

        sections.remove(section);

    }

    private Section findSection(final Station station) {
        return sections.stream()
                .filter(v -> matchesUpStation(station, v))
                .findAny()
                .orElseGet(() -> sections.get(sections.size() - 1));
    }

    private boolean matchesUpStation(final Station station, final Section section) {
        return section.getUpStation().equals(station);
    }

    private boolean isFirstOrLastStation(final Station station) {
        final List<Station> stations = findAllStationsInOrder();
        return isFirstStation(station, stations) || isLastStation(station, stations);
    }

    public boolean isFirstStation(final Station station, final List<Station> stations) {
        return stations.get(0).equals(station);
    }

    private boolean isLastStation(final Station station, final List<Station> stations) {
        return getLastStation(stations).equals(station);
    }

    private Station getLastStation(final List<Station> stations) {
        return stations.get(stations.size() - 1);
    }

    private void updateMiddleSection(final Section section) {
        final Section beforeSection = sections.get(sections.indexOf(section) - 1);
        beforeSection.updateDownStationAndDistance(section.getDownStation(), section.getDistance() + beforeSection.getDistance());
    }
}
