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

    public boolean isEmpty() {
        return sections.isEmpty();
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
        if (!containsStation(station)) {
            throw new IllegalArgumentException("Station not exists");
        }


        if (isSingleSection()) {
            throw new IllegalArgumentException("Last section cannot be removed");
        }

        final Section section = findSection(station);

        // 1, 2 -> 2, 3 -> 3, 4 -> 4, 5
        if (!isFirstOrLastStation(station)) {
            updateMiddleSection(section);
        }

        sections.remove(section);
    }

    private boolean containsStation(final Station station) {
        return findAllStationsInOrder().contains(station);
    }

    private boolean isSingleSection() {
        return sections.size() == 1;
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
        return stations.get(stations.size() - 1).equals(station);
    }

    private void updateMiddleSection(final Section section) {
        final Section beforeSection = sections.get(sections.indexOf(section) - 1);
        beforeSection.updateDownStationAndDistance(section.getDownStation(), section.getDistance() + beforeSection.getDistance());
    }

    public int getDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public void add(final Section section) {
        if (isEmpty()) {
            sections.add(section);
            return;
        }

        final Set<Station> stations = new HashSet<>(findAllStationsInOrder());
        if (isDuplicated(section, stations) || isDisconnectedSection(section, stations)) {
            throw new IllegalArgumentException("Invalid Section");
        }

        sections.stream()
                .filter(v -> v.getUpStation().equals(section.getUpStation()) || v.getDownStation().equals(section.getDownStation()))
                .findAny()
                .ifPresentOrElse(v -> addBetween(section, v), () -> sections.add(section));
    }

    private boolean isDisconnectedSection(final Section section, final Set<Station> stations) {
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    private boolean isDuplicated(final Section section, final Set<Station> stations) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private void addBetween(final Section section, final Section targetSection) {
        if (matchesUpStation(targetSection.getUpStation(), section)) {
            targetSection.updateUpStationAndDistance(section.getDownStation(), calculateDistance(section, targetSection));
            sections.add(sections.indexOf(targetSection), section);
        } else {
            targetSection.updateDownStationAndDistance(section.getUpStation(), calculateDistance(section, targetSection));
            sections.add(sections.indexOf(targetSection), section);
        }
    }

    private int calculateDistance(final Section section, final Section targetSection) {
        final int distance = targetSection.getDistance() - section.getDistance();
        if (distance <= 0) {
            throw new IllegalArgumentException("Invalid Section");
        }
        return distance;
    }

}
