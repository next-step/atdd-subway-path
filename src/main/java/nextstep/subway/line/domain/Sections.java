package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.EmptyLineException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.SectionAlreadyRegisteredException;
import nextstep.subway.line.exception.SectionNotSearchedException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public int size() {
        return sections.size();
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        validateAddSection(section);
        addFirstSection(section);
        addMiddleSections(section);
        addLastSection(section);
    }

    public void removeSection(Station station) {
        validateRemoveSections(station);
        sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        System.out.println(sections.stream()
                .map(Section::getUpStation)
                .map(Station::getName)
                .collect(Collectors.toList()));
        System.out.println(sections.stream()
                .map(Section::getDownStation)
                .map(Station::getName)
                .collect(Collectors.toList()));
        stations.add(getLastDownStation());
        return stations;
    }

    private Section searchSection(Section section) {
        return sections.stream()
                .filter(section::search)
                .findAny()
                .orElseThrow(SectionNotSearchedException::new);
    }

    private void validateAddSection(Section section) {
        if (isSectionAlreadyRegistered(section)) {
            throw new SectionAlreadyRegisteredException();
        }
    }

    private void validateRemoveSections(Station station) {
        if (sections.size() <= 1) {
            throw new EmptyLineException();
        }
        validateUpStation(station);
    }

    private void validateUpStation(Station station) {
        if (!getLastDownStation().equals(station)) {
            throw new NotLastStationException();
        }
    }

    private void addMiddleSections(Section section) {
        Section searchedSection = searchSection(section);
        if (!isSectionAlreadyRegistered(section)
                && searchedSection.isSplittable(section)) {
            sections.remove(searchedSection);
            sections.addAll(searchedSection.split(section));
        }
    }

    private void addFirstSection(Section section) {
        if (!isSectionAlreadyRegistered(section)
                && getFirstUpStation().equals(section.getDownStation())) {
            sections.add(FIRST_INDEX, section);
        }
    }

    private void addLastSection(Section section) {
        if (!isSectionAlreadyRegistered(section)
                && getLastDownStation().equals(section.getUpStation())) {
            sections.add(section);
        }
    }

    private Station getFirstUpStation() {
        return sections.get(FIRST_INDEX).getUpStation();
    }

    private Station getLastDownStation() {
        int lastIndex = sections.size() - 1;
        return sections.get(lastIndex).getDownStation();
    }

    private boolean isSectionAlreadyRegistered(Section section) {
        return sections.stream()
                .anyMatch(section::isRegistered);
    }
}
