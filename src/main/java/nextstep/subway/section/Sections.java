package nextstep.subway.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void updateLine(final Line line) {
        this.sections.get(0).updateLine(line);
    }

    public void add(final Section section) {
        validateIntersection(section);

        Station upEndStation = getUpEndStation();
        updateSectionIfInsert(section, upEndStation);

        sections.add(section);
    }

    public void delete(final Station station) {
        if (sections.size() == 1) {
            throw new BusinessException("상행 종점역과 하행 종점역만 있는 노선의 구간은 삭제할 수 없습니다.");
        }

        Section firstSection = findUpSectionByStation(getUpEndStation())
            .orElseThrow();
        Section lastSection = getLastSection();
        Section section = findUpSectionByStation(station)
            .orElse(lastSection);

        if (isInMiddle(station, firstSection, lastSection)) {
            Section prevSection = findDownSectionByStation(section.getUpStation());
            prevSection.merge(section.getDownStation(), section.getDistance());
            sections.remove(section);
            return;
        }

        sections.remove(section);
    }

    public Section getLastSection() {
        Map<Station, Section> upSectionMap = getUpSectionMap();

        Station currentUpStation = getUpEndStation();
        Section lastSection = upSectionMap.get(currentUpStation);
        while (upSectionMap.containsKey(currentUpStation)) {
            lastSection = upSectionMap.get(currentUpStation);
            currentUpStation = lastSection.getDownStation();
        }

        return lastSection;
    }

    public List<Station> getStations() {
        Map<Station, Section> upSectionMap = getUpSectionMap();

        return getOrderedStations(upSectionMap);
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public Stream<Section> stream() {
        return this.sections.stream();
    }

    private boolean isInMiddle(
        final Station station,
        final Section firstSection,
        final Section lastSection) {
        return !station.equals(firstSection.getUpStation()) && !station.equals(
            lastSection.getDownStation());
    }

    private Optional<Section> findUpSectionByStation(final Station station) {
        Map<Station, Section> upSectionMap = getUpSectionMap();

        return Optional.ofNullable(upSectionMap.get(station));
    }

    private Section findDownSectionByStation(final Station station) {
        Map<Station, Section> downSectionMap = new HashMap<>();
        for (Section oldSection : sections) {
            downSectionMap.put(oldSection.getDownStation(), oldSection);
        }

        return downSectionMap.get(station);
    }

    private Map<Station, Section> getUpSectionMap() {
        Map<Station, Section> upSectionMap = new HashMap<>();
        for (Section oldSection : sections) {
            upSectionMap.put(oldSection.getUpStation(), oldSection);
        }
        return upSectionMap;
    }

    private void updateSectionIfInsert(final Section section, final Station upEndStation) {
        Map<Station, Section> upSectionMap = getStationToUpSectionMap(section);

        Station currentUpStation = upEndStation;
        while (upSectionMap.containsKey(currentUpStation)) {
            Section currentSection = upSectionMap.get(currentUpStation);

            if (isSameUpStations(section, currentSection)
                || isSameDownStations(section, currentSection)) {

                currentSection.update(section);
                break;
            }
            currentUpStation = currentSection.getDownStation();
        }
    }

    private void validateIntersection(final Section section) {
        List<Station> stations = new ArrayList<>(List.of(sections.get(0).getUpStation()));
        stations.addAll(
            sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));

        if (!stations.contains(section.getUpStation())
            && !stations.contains(section.getDownStation())) {

            throw new BusinessException(
                String.format("상행역과 하행역 중 하나는 등록되어 있어야 합니다. 상행역ID: %s, 하행역ID: %s",
                    section.getUpStation().getId(), section.getDownStation().getId()));
        }
    }

    private Map<Station, Section> getStationToUpSectionMap(final Section section) {
        Map<Station, Section> upSectionMap = new HashMap<>();
        for (Section oldSection : sections) {
            upSectionMap.put(oldSection.getUpStation(), oldSection);
            sameThenThrow(section, oldSection);
        }

        return upSectionMap;
    }

    private void sameThenThrow(final Section section, final Section oldSection) {
        if (isSameUpStations(section, oldSection) && isSameDownStations(section, oldSection)) {

            throw new BusinessException(
                String.format("이미 등록되어 있는 구간입니다. 상행역ID: %s, 하행역ID: %s",
                    section.getUpStation().getId(),
                    section.getDownStation().getId()));
        }
    }

    private boolean isSameDownStations(final Section section, final Section oldSection) {
        return oldSection.getDownStation().equals(section.getDownStation());
    }

    private boolean isSameUpStations(final Section section, final Section oldSection) {
        return oldSection.getUpStation().equals(section.getUpStation());
    }

    private Station getUpEndStation() {
        Set<Station> upStations = new HashSet<>();
        Set<Station> downStations = new HashSet<>();

        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }

        for (Station upStation : upStations) {
            if (!downStations.contains(upStation)) {
                return upStation;
            }
        }

        throw new BusinessException("상행 종점역을 찾을 수 없습니다.");
    }

    private List<Station> getOrderedStations(final Map<Station, Section> upSectionMap) {
        Station upEndStation = getUpEndStation();
        List<Station> stations = new ArrayList<>(List.of(upEndStation));

        Station currentUpStation = upEndStation;
        while (upSectionMap.containsKey(currentUpStation)) {
            Section currentSection = upSectionMap.get(currentUpStation);
            stations.add(currentSection.getDownStation());
            currentUpStation = currentSection.getDownStation();
        }

        return stations;
    }
}
