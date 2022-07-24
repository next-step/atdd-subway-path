package nextstep.subway.domain;

import nextstep.subway.exception.sections.SectionsAddException;
import nextstep.subway.exception.sections.SectionsDeleteException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    private static final int FIRST_SECTION_INDEX = 0;
    private static final int MINIMUM_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateSection(section);
        addSection(section);
    }

    public List<Station> getStations() {
        if (isEmptySections()) {
            return Collections.emptyList();
        }

        return getOrderedStations();
    }

    public void deleteSection(Station station) {
        if (isSizeLessThanOne()) {
            throw SectionsDeleteException.CANT_DELETE_LAST_ONE_SECTION_EXCEPTION();
        }

        Section firstSection = getFirstSection();
        if (firstSection.hasSameUpStation(station)) {
            sections.remove(firstSection);
            return;
        }

        Section lastSection = getLastSection();
        if (lastSection.hasSameDownStation(station)) {
            sections.remove(lastSection);
            return;
        }

        deleteMiddleStation(station);
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
    }

    private boolean isSizeLessThanOne() {
        return sections.size() <= MINIMUM_SECTIONS_SIZE;
    }

    private void deleteMiddleStation(Station station) {
        Section sameUpStationSection = findSameUpStationSection(station);
        Section sameDownStationSection = findSameDownStationSection(station);
        if (sameUpStationSection == null || sameDownStationSection == null) {
            return;
        }

        Section newSection = Section.combineOf(sameDownStationSection, sameUpStationSection);
        sections.remove(sameUpStationSection);
        sections.remove(sameDownStationSection);
        sections.add(newSection);
    }

    private void addSection(Section section) {
        if (isPossibleAddFrontOrBack(section)) {
            sections.add(section);
            return;
        }

        Section findSection = findTargetSection(section);
        Section halfSection = findSection.divideSectionByMiddle(section);
        sections.remove(findSection);
        sections.add(halfSection);
        sections.add(section);
    }

    private void validateSection(Section section) {
        if (hasBothUpAndDownStations(section)) {
            throw SectionsAddException.ALREADY_BOTH_STATION_REGISTER_EXCEPTION();
        }

        if (hasNotSameStationIn(section)) {
            throw SectionsAddException.NOT_FOUND_BOTH_STATION_EXCEPTION();
        }
    }

    private boolean isPossibleAddFrontOrBack(Section section) {
        return isSectionsUpStation(section.getDownStation()) || isSectionsDownStation(section.getUpStation());
    }

    private Section findTargetSection(Section section) {
        return sections.stream()
                .filter(s -> s.hasSameUpStation(section.getUpStation()) || s.hasSameDownStation(section.getDownStation()))
                .findFirst()
                .orElseThrow(SectionsDeleteException::NOT_FOUND_LAST_SECTION_EXCEPTION);
    }

    private Section findSameDownStationSection(Station station) {
        return sections.stream()
                .filter(s -> s.hasSameDownStation(station))
                .findFirst()
                .orElseThrow(SectionsDeleteException::NOT_FOUND_STATION_EXCEPTION);
    }

    private Section findSameUpStationSection(Station station) {
        return sections.stream()
                .filter(s -> s.hasSameUpStation(station))
                .findFirst()
                .orElseThrow(SectionsDeleteException::NOT_FOUND_STATION_EXCEPTION);
    }

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(station))
                .findFirst()
                .map(Section::getDownStation)
                .orElse(station);
    }

    private List<Station> getOrderedStations() {
        final List<Station> stations = new ArrayList<>();
        Station upStation = getFirstUpStation();

        stations.add(upStation);
        for (int i = 0; i < sections.size(); i++) {
            Station nextStation = findNextStation(upStation);
            stations.add(nextStation);
            upStation = nextStation;
        }

        return Collections.unmodifiableList(stations);
    }

    private boolean hasNotSameStationIn(Section section) {
        return sections.stream().noneMatch(s -> s.hasStationIn(section));
    }

    private boolean hasBothUpAndDownStations(Section section) {
        return hasSameUpStation(section.getUpStation()) && hasSameDownStation(section.getDownStation());
    }

    private boolean hasSameUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStation(station));
    }

    private boolean hasSameDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasSameDownStation(station));
    }

    private boolean isSectionsUpStation(Station station) {
        return getFirstUpStation().equals(station);
    }

    private boolean isSectionsDownStation(Station station) {
        return getLastDownStation().equals(station);
    }

    private Section getFirstSection() {
        Station firstUpStation = getFirstUpStation();
        return findSameUpStationSection(firstUpStation);
    }

    private Section getLastSection() {
        Station lastDownStation = getLastDownStation();
        return findSameDownStationSection(lastDownStation);
    }

    private Station getFirstUpStation() {
        Station startStation = sections.get(FIRST_SECTION_INDEX).getUpStation();
        return getFirstUpStation(startStation);
    }

    private Station getFirstUpStation(Station startStation) {
        return sections.stream()
                .filter(section -> section.hasSameDownStation(startStation))
                .findFirst()
                .map(Section::getUpStation)
                .map(this::getFirstUpStation)
                .orElse(startStation);
    }

    private Station getLastDownStation() {
        Station station = sections.get(FIRST_SECTION_INDEX).getDownStation();
        return getLastDownStation(station);
    }

    private Station getLastDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(downStation))
                .findFirst()
                .map(Section::getDownStation)
                .map(this::getLastDownStation)
                .orElse(downStation);
    }
}
