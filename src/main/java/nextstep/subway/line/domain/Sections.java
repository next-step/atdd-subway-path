package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.EmptyLineException;
import nextstep.subway.line.exception.SectionAlreadyRegisteredException;
import nextstep.subway.line.exception.SectionMergeFailedException;
import nextstep.subway.line.exception.SectionNotSearchedException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int SINGLE_SIZE = 1;

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
        if (sections.size() <= SINGLE_SIZE) {
            throw new EmptyLineException();
        }
        List<Section> searchedSections = searchSections(station);
        sections.removeAll(searchedSections);
        if (searchedSections.size() > SINGLE_SIZE) {
            sections.add(mergeSections(searchedSections, station));
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Map<Station, Station> downMapper = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        Station downStation = getFirstUpStation();
        while (downStation != null) {
            stations.add(downStation);
            downStation = downMapper.get(downStation);
        }

        return stations;
    }

    private List<Section> searchSections(Station... stations) {
        List<Section> searchedSections = sections.stream()
                .filter(section -> Arrays.stream(stations).anyMatch(section::hasStation))
                .collect(Collectors.toList());
        if (searchedSections.isEmpty()) {
            throw new SectionNotSearchedException();
        }
        return searchedSections;
    }

    private Section mergeSections(List<Section> sections, Station station) {
        Section upSection = sections.stream().filter(
                section -> station.equals(section.getDownStation())
        ).findAny().orElseThrow(SectionMergeFailedException::new);
        Section downSection = sections.stream().filter(
                section -> station.equals(section.getUpStation())
        ).findAny().orElseThrow(SectionMergeFailedException::new);
        return upSection.merge(downSection);

    }

    private void validateAddSection(Section section) {
        if (isSectionAlreadyRegistered(section)) {
            throw new SectionAlreadyRegisteredException();
        }
    }

    private void addMiddleSections(Section section) {
        Section searchedSection = searchSections(
                section.getUpStation(), section.getDownStation()
        ).get(FIRST_INDEX);
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
        Map<Station, Station> upMapper = sections.stream()
                .collect(Collectors.toMap(Section::getDownStation, Section::getUpStation));
        Station station = sections.get(FIRST_INDEX).getUpStation();
        while (upMapper.get(station) != null) {
            station = upMapper.get(station);
        }
        return station;
    }

    private Station getLastDownStation() {
        Map<Station, Station> downMapper = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Station station = sections.get(FIRST_INDEX).getDownStation();
        while (downMapper.get(station) != null) {
            station = downMapper.get(station);
        }
        return station;
    }

    private boolean isSectionAlreadyRegistered(Section section) {
        return sections.stream()
                .anyMatch(section::isRegistered);
    }
}
