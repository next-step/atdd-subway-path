package nextstep.subway.domain;

import nextstep.subway.domain.exception.line.LineHasBothStationsException;
import nextstep.subway.domain.exception.line.NewSectionCouldHaveAnyRegisteredStation;

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
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validate(section);

        if (isMiddelSection(section)) {
            updateOldSection(section);

        }

        sections.add(section);
    }

    private void updateOldSection(Section section) {
        getOldSection(section).update(section);
    }

    private boolean isMiddelSection(final Section section) {
        return !isFirstSection(section) && !isLastSection(section);
    }

    private boolean isLastSection(final Section section) {
        return getTerminalStation().equals(section.getUpStation());
    }

    private boolean isFirstSection(final Section section) {
        return getFirstStationOrNull().equals(section.getDownStation());
    }

    private void validate(final Section section) {
        final List<Station> stations = getStations();
        final boolean hasUpStation = hasStation(stations, section.getUpStation());
        final boolean hasDownStation = hasStation(stations, section.getDownStation());

        if (hasUpStation && hasDownStation) {
            throw new LineHasBothStationsException();
        }

        if (!(hasUpStation || hasDownStation)) {
            throw new NewSectionCouldHaveAnyRegisteredStation();
        }
    }

    private static boolean hasStation(final List<Station> stations, final Station station) {
        return stations.stream()
                .anyMatch(s -> s.equals(station));
    }

    private Section getOldSection(final Section section) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation())
                        || s.getDownStation().equals(section.getDownStation())
                )
                .findFirst()
                .orElseThrow(NewSectionCouldHaveAnyRegisteredStation::new);
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
