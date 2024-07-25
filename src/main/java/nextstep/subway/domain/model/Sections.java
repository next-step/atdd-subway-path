package nextstep.subway.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.springframework.data.util.Pair;

import nextstep.subway.domain.service.SectionAdditionStrategy;

@Embeddable
public class Sections {
    public static final String ALREADY_EXISTING_SECTION_MESSAGE = "이미 추가된 구간입니다.";
    public static final String CANNOT_ADD_SAME_STATIONS_MESSAGE = "상행역과 하행역이 같습니다.";
    public static final String CANNOT_ADD_SECTION_MESSAGE = "구간을 추가할 수 없습니다.";
    public static final String NO_SECTION_TO_REMOVE_STATION_MESSAGE = "역을 삭제할 구간이 존재하지 않습니다.";
    public static final String LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE = "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없습니다.";
    public static final String ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION_MESSAGE = "인덱스가 범위를 벗어났습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Station> getOrderedUnmodifiableStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        Section firstSection = getFirstSection(stationToSectionMaps.getSecond());
        List<Station> orderedStations = getOrderedStations(firstSection, stationToSectionMaps.getFirst());
        return Collections.unmodifiableList(orderedStations);
    }

    private List<Station> getOrderedStations(Section firstSection, Map<Station, Section> upStationToSectionMap) {
        List<Station> orderedStations = new ArrayList<>();
        Section currentSection = firstSection;

        while (currentSection != null) {
            orderedStations.add(currentSection.getUpStation());
            Section nextSection = upStationToSectionMap.get(currentSection.getDownStation());

            if (nextSection == null) {
                orderedStations.add(currentSection.getDownStation());
            }

            currentSection = nextSection;
        }

        return orderedStations;
    }

    public List<Section> getOrderedUnmodifiableSections() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        Section firstSection = getFirstSection(stationToSectionMaps.getSecond());
        List<Section> orderedSections = getOrderedSections(firstSection, stationToSectionMaps.getFirst());
        return Collections.unmodifiableList(orderedSections);
    }

    public Pair<Map<Station, Section>, Map<Station, Section>> getStationToSectionMaps() {
        Map<Station, Section> upStationToSectionMap = new HashMap<>();
        Map<Station, Section> downStationToSectionMap = new HashMap<>();

        for (Section section : sections) {
            upStationToSectionMap.put(section.getUpStation(), section);
            downStationToSectionMap.put(section.getDownStation(), section);
        }

        return Pair.of(upStationToSectionMap, downStationToSectionMap);
    }

    public boolean equalsWithFirstSection(Section section) {
        if (sections.isEmpty() || section == null) {
            return false;
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        return section.equals(getFirstSection(stationToSectionMaps.getSecond()));
    }

    public boolean equalsWithLastSection(Section section) {
        if (sections.isEmpty() || section == null) {
            return false;
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        return section.equals(getLastSection(stationToSectionMaps.getFirst()));
    }


    private Section getFirstSection(Map<Station, Section> downStationToSectionMap) {
        return sections
            .stream()
            .filter(section -> !downStationToSectionMap.containsKey(section.getUpStation()))
            .findFirst()
            .orElse(null);
    }

    private Section getLastSection(Map<Station, Section> upStationToSectionMap) {
        return sections
            .stream()
            .filter(section -> !upStationToSectionMap.containsKey(section.getDownStation()))
            .findFirst()
            .orElse(null);
    }

    private List<Section> getOrderedSections(
        Section firstSection,
        Map<Station, Section> upstationToSectionMap
    ) {
        List<Section> orderedSections = new ArrayList<>();
        Section currentSection = firstSection;

        while (currentSection != null) {
            orderedSections.add(currentSection);
            currentSection = upstationToSectionMap.get(currentSection.getDownStation());
        }

        return orderedSections;
    }

    public void addSection(Line line, Section newSection) {
        validateSectionAddition(newSection);
        newSection.updateLine(line);
        sections.add(newSection);
    }

    public void addSection(SectionAdditionStrategy sectionAdditionStrategy, Line line, Section newSection) {
        validateSectionAddition(newSection);
        newSection.updateLine(line);
        sectionAdditionStrategy.addSection(line, newSection);
    }

    private void validateSectionAddition(Section newSection) {
        if (sections.contains(newSection)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_SECTION_MESSAGE);
        }

        if (newSection.areBothStationsSame()) {
            throw new IllegalArgumentException(Sections.CANNOT_ADD_SAME_STATIONS_MESSAGE);
        }
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException(LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE);
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();

        removeStationFromSections(
            stationToSectionMaps.getSecond().get(station),
            stationToSectionMaps.getFirst().get(station)
        );
    }

    private void removeStationFromSections(Section firstSection, Section secondSection) {
        // 중간에 있는 역을 삭제하는 경우
        if (firstSection != null && secondSection != null) {
            firstSection.updateDownStation(secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance());
            sections.remove(secondSection);
            return;
        }

        // 가장 마지막 구간의 하행역을 삭제하는 경우
        if (firstSection != null) {
            sections.remove(firstSection);
            return;
        }

        // 가장 첫 구간의 상행역을 삭제하는 경우
        if (secondSection != null) {
            sections.remove(secondSection);
            return;
        }

        throw new IllegalArgumentException(NO_SECTION_TO_REMOVE_STATION_MESSAGE);
    }

    public List<Section> toUnmodifiableList() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(sections);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Section> getSections() {
        return sections;
    }

    public int size() {
        return sections.size();
    }

    public Section get(int index) {
        if (index < 0 || index >= sections.size()) {
            throw new ArrayIndexOutOfBoundsException(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION_MESSAGE);
        }

        return sections.get(index);
    }
}
