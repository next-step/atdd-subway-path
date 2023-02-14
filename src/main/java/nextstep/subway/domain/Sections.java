package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static nextstep.subway.domain.Station.DEFAULT_STATION;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {PERSIST, MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        final List<Section> orderedSections = getOrderedSections();
        return orderedSections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getOrderedSections(){
        List<Section> orderedSections = new ArrayList<>();

        Section section;
        Station upStation = getFirstStationOrNull();
        while ((section = getSectionOrNullByUpStation(upStation)) != null) {
            upStation = section.getDownStation();
            orderedSections.add(section);
        }

        return orderedSections;
    }

    private Station getFirstStationOrNull() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(station -> sections.stream()
                        .noneMatch(sec -> sec.getDownStation().equals(station)))
                .findFirst()
                .orElse(null);
    }

    private Section getSectionOrNullByUpStation(final Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElse(null);
    }

    public Station getTerminalStation() {
        final List<Section> orderedSections = getOrderedSections();
        if (orderedSections.isEmpty()) {
            return DEFAULT_STATION;
        }

        return orderedSections.get(orderedSections.size() - 1).getDownStation();
    }

    public void removeLastSection() {
        sections.remove(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
