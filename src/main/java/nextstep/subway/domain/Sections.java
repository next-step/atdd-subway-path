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
    private static final String NOT_EXIST_SECTIONS_EXCEPTION = "삭제할 Sections이 존재하지 않습니다.";
    private static final String NOT_SAME_DOWN_STATION_EXCEPTION = "마지막 구간의 하행종점역이 삭제할 하행종점역과 일치하지 않습니다";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean hasUpStation = hasSameUpStation(section.getUpStation());
        boolean hasDownStation = hasSameDownStation(section.getDownStation());
        if (hasUpStation && hasDownStation) {
            throw new SectionsException(ErrorCode.ALREADY_BOTH_STATION_REGISTER_EXCEPTION);
        }

        if (isSectionsUpStation(section.getDownStation())) {
            sections.add(section);
            return;
        }

        if (isSectionsDownStation(section.getUpStation())) {
            sections.add(section);
            return;
        }

        Section findSection = findTargetSection(section);
        if (section.isGreaterThanDistance(findSection.getDistance())) {
            throw new SectionsException(ErrorCode.SECTION_DISTANCE_EXCEPTION);
        }

        Section halfSection = findSection.divideSectionByMiddle(section);
        sections.remove(findSection);
        sections.add(halfSection);
        sections.add(section);
    }

    public List<Station> getStations() {
        if (isEmptySections()) {
            return Collections.emptyList();
        }

        final List<Station> stations = new ArrayList<>();
        Station firstUpStation = getFirstUpStation();
        addStationByOrder(stations, firstUpStation);

        return Collections.unmodifiableList(stations);
    }

    public void deleteLastSection(Station station) {
        if (isEmptySections()) {
            throw new IllegalStateException(NOT_EXIST_SECTIONS_EXCEPTION);
        }

        Section lastSection = getLastSection();
        if (lastSection.hasNotDownStation(station)) {
            throw new IllegalStateException(NOT_SAME_DOWN_STATION_EXCEPTION);
        }

        sections.remove(lastSection);
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
    }

    private Section findTargetSection(Section section) {
        return sections.stream()
                .filter(s -> s.hasSameUpStation(s.getUpStation()) || s.hasSameDownStation(s.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new SectionsException(ErrorCode.NOT_FOUND_SECTION_EXCEPTION));
    }

    private void addStationByOrder(List<Station> stations, Station upStation) {
        stations.add(upStation);

        for (int i = 0; i < sections.size(); i++) {
            Station nextStation = findNextStation(upStation);
            stations.add(nextStation);
            upStation = nextStation;
        }
    }

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(station))
                .findFirst()
                .map(Section::getDownStation)
                .orElse(station);
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
        return getFirstSection().hasSameUpStation(station);
    }

    private boolean isSectionsDownStation(Station station) {
        return getLastSection().hasSameDownStation(station);
    }

    private Section getFirstSection() {
        return sections.get(FIRST_SECTION_INDEX);
    }

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    private Station getFirstUpStation() {
        Station startStation = getFirstSection().getUpStation();
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
}
