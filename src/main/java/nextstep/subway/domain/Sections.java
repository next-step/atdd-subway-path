package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

}
