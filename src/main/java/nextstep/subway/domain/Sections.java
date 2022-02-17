package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int SECTION_MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validationNewSection(section);
        updateExistingSection(section);

        sections.add(section);
    }


    public void remove(Station station) {
        validationRemoveStation(station);

        Optional<Section> sectionHasUpStation = findSectionHasUpStationAs(station);
        Optional<Section> sectionHasDownStation = findSectionHasDownStationAs(station);

        sectionHasUpStation.ifPresent(section -> sections.remove(section));
        sectionHasDownStation.ifPresent(section -> sections.remove(section));

        if (sectionHasUpStation.isPresent() && sectionHasDownStation.isPresent()) {
            mergeExistingSections(sectionHasUpStation.get(), sectionHasDownStation.get());
        }

    }

    private Optional<Section> findSectionHasDownStationAs(Station station) {
        return sections.stream()
            .filter(s -> s.hasUpStationAs(station))
            .findAny();
    }

    private Optional<Section> findSectionHasUpStationAs(Station station) {
        return sections.stream()
            .filter(s -> s.hasDownStationAs(station))
            .findAny();
    }

    private List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        stations.addAll(
            sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList())
        );
        return stations;
    }

    public List<Station> getSortedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = new ArrayList<>();
        Station station = getFirstSection().getUpStation();
        Map<Station, Section> stationSectionMap = sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Function.identity()));

        while (stationSectionMap.containsKey(station)) {
            stations.add(station);
            station = stationSectionMap.get(station).getDownStation();
        }

        stations.add(station);
        return stations;
    }

    private Section getFirstSection() {
        Set<Station> downStations = sections.stream().map(Section::getDownStation).collect(Collectors.toSet());
        return sections.stream()
            .filter(s -> !downStations.contains(s.getUpStation()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section getLastSection() {
        Set<Station> upStations = sections.stream().map(Section::getUpStation).collect(Collectors.toSet());
        return sections.stream()
            .filter(s -> !upStations.contains(s.getDownStation()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    private boolean isAddSectionToFirst(Section section) {
        Section firstSection = getFirstSection();
        return firstSection.getUpStation().equals(section.getDownStation());
    }

    private boolean isAddSectionToLast(Section section) {
        Section lastSection = getLastSection();
        return lastSection.getDownStation().equals(section.getUpStation());
    }

    private void updateExistingSection(Section section) {
        if (isAddSectionToFirst(section) || isAddSectionToLast(section)) {
            return;
        }

        Section targetSection = sections.stream()
            .filter(s -> s.getUpStation().equals(section.getUpStation()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);

        if (section.getDistance() >= targetSection.getDistance()) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }

        targetSection.update(section.getDownStation(), section.getDistance());
    }

    private void validationNewSection(Section section) {
        long existStationCount = getStations().stream()
            .filter(s -> section.getUpStation().equals(s) || section.getDownStation().equals(s))
            .count();

        if (existStationCount == 2) {
            throw new IllegalArgumentException("중복 되는 구간입니다.");
        }

        if (existStationCount == 0) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나는 노선에 포함되어 있어야 합니다.");
        }
    }

    private void validationRemoveStation(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("노선에 존재하는 역만 삭제할 수 있습니다.");
        }

        if (sections.size() <= SECTION_MIN_SIZE) {
            throw new IllegalArgumentException("상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없습니다.");
        }
    }

    private void mergeExistingSections(Section firstSection, Section secondSection) {
        sections.add(firstSection.merge(secondSection));
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sections);
    }
}
