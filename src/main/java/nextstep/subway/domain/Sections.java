package nextstep.subway.domain;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.sections.SectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    private static final int FIRST_SECTION_INDEX = 0;

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

    public void deleteLastSection(Station station) {
        if (isEmptySections()) {
            throw new SectionsException(ErrorCode.NOT_FOUND_SECTION_EXCEPTION);
        }

        Section lastSection = getLastSection();
        if (lastSection.hasNotDownStation(station)) {
            throw new SectionsException(ErrorCode.NOT_SAME_DOWN_STATION_EXCEPTION);
        }

        sections.remove(lastSection);
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
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
            throw new SectionsException(ErrorCode.ALREADY_BOTH_STATION_REGISTER_EXCEPTION);
        }

        if (hasNotSameStationIn(section)) {
            throw new SectionsException(ErrorCode.NOT_FOUND_BOTH_STATION_EXCEPTION);
        }
    }

    private boolean isPossibleAddFrontOrBack(Section section) {
        return isSectionsUpStation(section.getDownStation()) || isSectionsDownStation(section.getUpStation());
    }

    private Section findTargetSection(Section section) {
        return sections.stream()
                .filter(s -> s.hasSameUpStation(section.getUpStation()) || s.hasSameDownStation(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new SectionsException(ErrorCode.NOT_FOUND_SECTION_EXCEPTION));
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

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(station))
                .findFirst()
                .map(Section::getDownStation)
                .orElse(station);
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

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
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
