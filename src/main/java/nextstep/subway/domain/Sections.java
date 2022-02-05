package nextstep.subway.domain;

import nextstep.subway.exception.IllegalUpdatingStateException;

import javax.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        checkStateToAddSection(section);

        if (addSectionBetweenStations(section)) {
            return;
        }
        if (addSectionInFrontOfFirstUpStation(section)) {
            return;
        }
        addSectionBehindLastDownStation(section);
    }

    private void checkStateToAddSection(Section section) {
        List<Station> allStations = getAllStations();
        if (allStations.contains(section.getUpStation()) && allStations.contains(section.getDownStation())) {
            throw new IllegalUpdatingStateException("요청한 구간의 상행역과 하행역 모두 이미 노선에 등록되어있어 등록이 불가합니다.");
        }
        if (!allStations.contains(section.getUpStation()) && !allStations.contains(section.getDownStation())) {
            throw new IllegalUpdatingStateException("요청한 구간의 상행역과 하행역 모두 기존 노선에 등록되어 있지않아 등록이 불가합니다.");
        }
    }

    private boolean addSectionBetweenStations(Section section) {
        if (addSectionBetweenStationsWhenSameUpStation(section)) {
            return true;
        }
        if (addSectionBetweenStationsWhenSameDownStation(section)) {
            return true;
        }
        return false;
    }

    private boolean addSectionBetweenStationsWhenSameUpStation(Section section) {
        Optional<Section> optionalSameUpStationSection = sections.stream()
                .filter(s -> s.getUpStation().equals(section.getUpStation()))
                .findAny();
        if (!optionalSameUpStationSection.isPresent()) {
            return false;
        }

        Section sameUpStationSection = optionalSameUpStationSection.get();
        sameUpStationSection.updateForSplittingBySameUpStationSection(section);
        sections.add(sections.indexOf(sameUpStationSection), section);
        return true;
    }

    private boolean addSectionBetweenStationsWhenSameDownStation(Section section) {
        Optional<Section> optionalSameDownStationSection = sections.stream()
                .filter(s -> s.getDownStation().equals(section.getDownStation()))
                .findAny();
        if (!optionalSameDownStationSection.isPresent()) {
            return false;
        }

        Section sameDownStationSection = optionalSameDownStationSection.get();
        sameDownStationSection.updateForSplittingBySameDownStationSection(section);
        sections.add(sections.indexOf(sameDownStationSection) + 1, section);
        return true;
    }

    private boolean addSectionInFrontOfFirstUpStation(Section section) {
        if (!getFirstUpStation().equals(section.getDownStation())) {
            return false;
        }
        sections.add(0, section);
        return true;
    }

    private boolean addSectionBehindLastDownStation(Section section) {
        if (!getLastDownStation().equals(section.getUpStation())) {
            return false;
        }
        sections.add(section);
        return true;
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> allStations = new ArrayList<>();
        Map<Station, Section> upStationSectionMap = getUpStationSectionMap();

        Section nowSection = getFirstSection();
        allStations.add(nowSection.getUpStation());
        for (; Objects.nonNull(nowSection); nowSection = upStationSectionMap.get(nowSection.getDownStation())) {
            allStations.add(nowSection.getDownStation());
        }

        return allStations;
    }

    public Section removeSection(Station station) {
        checkPossibleRemovingSection(station);
        return sections.remove(sections.size() - 1);
    }

    private void checkPossibleRemovingSection(Station station) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalUpdatingStateException("해당 노선의 구간이 " + MIN_SECTION_SIZE + "개 이하라 삭제하지 못합니다.");
        }
        if (!Objects.equals(getLastDownStation(), station)) {
            throw new IllegalUpdatingStateException("해당 노선의 하행 종점역이 아니라 삭제하지 못합니다.");
        }
    }

    public int getTotalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public Station getFirstUpStation() {
        return getFirstSection().getUpStation();
    }

    private Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("첫 번째 구간을 찾으려면 등록된 구간이 있어야합니다.");
        }
        Map<Station, Section> upStationSectionMap = getUpStationSectionMap();
        for (Station downStation : getDownStationSectionMap().keySet()) {
            upStationSectionMap.remove(downStation);
        }
        if (upStationSectionMap.size() != 1) {
            throw new IllegalStateException("첫 번째 구간을 찾을 수 없습니다. upStationSectionMap.size():"
                    + upStationSectionMap.size());
        }
        return upStationSectionMap.values().stream()
                .findFirst()
                .get();
    }

    public Station getLastDownStation() {
        return getLastSection().getDownStation();
    }

    private Section getLastSection() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("마지막 구간을 찾으려면 등록된 구간이 있어야합니다.");
        }
        Map<Station, Section> downStationSectionMap = getDownStationSectionMap();
        for (Station downStation : getUpStationSectionMap().keySet()) {
            downStationSectionMap.remove(downStation);
        }
        if (downStationSectionMap.size() != 1) {
            throw new IllegalStateException("마지막 섹션을 찾을 수 없습니다. downStationSectionMap.size():"
                    + downStationSectionMap.size());
        }
        return downStationSectionMap.values().stream()
                .findFirst()
                .get();
    }

    private Map<Station, Section> getUpStationSectionMap() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Function.identity()));
    }

    private Map<Station, Section> getDownStationSectionMap () {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStation, Function.identity()));
    }
}
