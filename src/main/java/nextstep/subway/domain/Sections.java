package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSize(sections);
        this.sections = new ArrayList<>(sections);
    }

    private void validateSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new ApplicationException("구간이 존재하지 않습니다.");
        }
    }

    public void validateRegisterStationBy(Station upStation, Station downStation) {
        boolean hasDuplicateSection = sections.stream()
                .anyMatch(section -> isSameSection(upStation, downStation, section));
        if (hasDuplicateSection) {
            throw new ApplicationException("신규 구간이 기존 구간과 일치하여 구간을 생성할 수 없습니다.");
        }
    }

    private boolean isSameSection(Station upStation, Station downStation, Section section) {
        return section.isSame(upStation, downStation);
    }

    public void addSectionInMiddle(Station upStation, Station downStation, long distance) {
        if (!isTerminalAddStation(upStation, downStation)) {
            updateExistingSection(upStation, downStation, distance);
        }
    }

    private void updateExistingSection(Station upStation, Station downStation, long distance) {
        Section matchingSection = findMatchingSection(upStation);
        if (matchingSection.isUpStation(upStation)) {
            matchingSection.changeUpStation(downStation, distance);
            return;
        }
        matchingSection.changeDownStation(upStation, distance);
    }

    public Section findMatchingSection(Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseGet(this::findLastSection);
    }

    private boolean isTerminalAddStation(Station upStation, Station downStation) {
        return isTerminalStation(upStation, downStation);
    }

    private boolean isTerminalStation(Station upStation, Station downStation) {
        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();
        return firstSection.isUpStation(downStation) || lastSection.isDownStation(upStation);
    }

    public void validateDeleteSection() {
        validateSectionCount();
    }

    private void validateSectionCount() {
        if (sections.size() == 1) {
            throw new ApplicationException("구간이 한개만 있을 경우 구간을 제거할 수 없습니다.");
        }
    }


    public Optional<Section> findDeleteSectionAtTerminal(Station station) {
        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();

        if (firstSection.isUpStation(station)) {
            return Optional.of(firstSection);
        }

        if (lastSection.isDownStation(station)) {
            return Optional.of(lastSection);
        }
        return Optional.empty();
    }

    public Section findDeleteStationPrevSection(Station station) {
        return findMatchingSectionEqualsDownStation(station);
    }

    public Section findDeleteStationNextSection(Station station) {
        return findMatchingSectionEqualsUpStation(station);
    }

    private Section findMatchingSectionEqualsUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("상행역이 존재하는 구간을 찾을 수 없습니다."));
    }

    private Section findMatchingSectionEqualsDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isDownStation(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("하행역이 존재하는 구간을 찾을 수 없습니다."));
    }

    private Section findLastSection() {
        List<Station> upStations = upStations();
        List<Station> downStations = downStations();

        Station downTerminalStation = downTerminalStation(downStations, upStations);
        return lastSection(downTerminalStation);
    }

    private Station downTerminalStation(List<Station> downStations, List<Station> upStations) {
        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("하행 종점역을 찾을 수 없습니다."));
    }

    private Section lastSection(Station downTerminalStation) {
        return sections.stream()
                .filter(section -> section.downStation().equals(downTerminalStation))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("하행 종점역을 포함하는 구간을 찾을 수 없습니다."));
    }

    private Section findFirstSection() {
        List<Station> upStations = upStations();
        List<Station> downStations = downStations();

        Station upTerminalStation = upTerminalStation(upStations, downStations);
        return firstSection(upTerminalStation);
    }

    private Station upTerminalStation(List<Station> upStations, List<Station> downStations) {
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("상행 종점역을 찾을 수 없습니다."));
    }

    private Section firstSection(Station upTerminalStation) {
        return sections.stream()
                .filter(section -> section.upStation().equals(upTerminalStation))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("상행 종점역을 포함하는 구간을 찾을 수 없습니다."));
    }

    private List<Station> upStations() {
        return sections.stream()
                .map(Section::upStation)
                .collect(Collectors.toList());
    }

    private List<Station> downStations() {
        return sections.stream()
                .map(Section::downStation)
                .collect(Collectors.toList());
    }

    public List<Station> sortedStations() {
        return Stream.concat(Stream.of(findFirstSection().upStation()), createSortedStation().stream())
                .collect(toUnmodifiableList());
    }

    private List<Station> createSortedStation() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Station> nextStation = createStationMapping();

        Station currentStation = findFirstSection().upStation();
        while (nextStation.containsKey(currentStation)) {
            currentStation = nextStation.get(currentStation);
            sortedStations.add(currentStation);
        }
        return sortedStations;
    }

    private Map<Station, Station> createStationMapping() {
        return sections.stream()
                .collect(Collectors.toMap(Section::upStation, Section::downStation));
    }

}
