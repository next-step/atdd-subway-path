package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int REMOVE_SIZE_MIN = 2;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }
        List<Section> upStationSection = getSectionsBy(section.getUpStation());
        List<Section> downStationSection = getSectionsBy(section.getDownStation());

        validateAddSection(upStationSection, downStationSection);

        if (upStationSection.isEmpty()) {
            addBasedOnDownStationSection(section);
        }
        if (downStationSection.isEmpty()) {
            addBasedOnUpStationSection(section);
        }
    }

    private void validateAddSection(final List<Section> upStationSection, final List<Section> downStationSection) {
        if (isAlreadyAddedSection(upStationSection, downStationSection)
                || isNonIncludeStation(upStationSection, downStationSection)) {
            throw new IllegalArgumentException("구간 추가 제약 조건을 위반했습니다.");
        }
    }

    private boolean isNonIncludeStation(
            final List<Section> upStationSection,
            final List<Section> downStationSection
    ) {
        return !sections.isEmpty() && upStationSection.isEmpty() && downStationSection.isEmpty();
    }

    private boolean isAlreadyAddedSection(
            final List<Section> upStationSection,
            final List<Section> downStationSection
    ) {
        return !upStationSection.isEmpty() && !downStationSection.isEmpty();
    }

    private void addBasedOnDownStationSection(final Section section) {
        if (section.getDownStation().equals(getLineUpStation())) {
            sections.add(section);
            return;
        }
        Section existingSection = findDownStationSection(section.getDownStation());
        validateDistanceAddSectionBetweenExistingSection(section, existingSection);
        existingSection
                .setDownStation(section.getUpStation())
                .setDistance(existingSection.getDistance() - section.getDistance());
        sections.add(section);
    }

    private Section findDownStationSection(final Station downStation) {
        return getSectionsBy(downStation).stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("역을 하행선으로 하는 구간이 존재하지 않습니다."));
    }

    private void addBasedOnUpStationSection(final Section section) {
        if (section.getUpStation().equals(getLineDownStation())) {
            sections.add(section);
            return;
        }
        Section existingSection = findUpStationSection(section.getUpStation());
        validateDistanceAddSectionBetweenExistingSection(section, existingSection);
        existingSection
                .setUpStation(section.getDownStation())
                .setDistance(existingSection.getDistance() - section.getDistance());
        sections.add(section);
    }

    private void validateDistanceAddSectionBetweenExistingSection(
            final Section newSection,
            final Section existingSection
    ) {
        if (newSection.getDistance() >= existingSection.getDistance()) {
            throw new IllegalArgumentException("기존 구간 사이에 새로운 구간 추가시 새로운 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
        }
    }

    public void remove(final Station station) {
        if (sections.size() < REMOVE_SIZE_MIN) {
            throw new IllegalArgumentException("구간 목록의 크기가 " + REMOVE_SIZE_MIN + " 보다 클 경우 구간 제거가 가능합니다.");
        }
        if (isLineUpStation(station) || isLineDownStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        Section stationBasedOnDownStation = findDownStationSection(station);
        Section stationBasedOnUpStation = findUpStationSection(station);
        sections.removeAll(List.of(stationBasedOnUpStation, stationBasedOnDownStation));

        Section relocationSection = relocationRemoveMiddleSection(stationBasedOnDownStation, stationBasedOnUpStation);
        sections.add(relocationSection);
    }

    private Section relocationRemoveMiddleSection(
            final Section stationBasedOnDownStation,
            final Section stationBasedOnUpStation
    ) {
        Line line = stationBasedOnUpStation.getLine();
        Station upStation = stationBasedOnDownStation.getUpStation();
        Station downStation = stationBasedOnUpStation.getDownStation();
        int distance = stationBasedOnDownStation.getDistance() + stationBasedOnUpStation.getDistance();
        return new Section(line, upStation, downStation, distance);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        Station upStation = getLineUpStation();
        for (int i = 0; i < sections.size(); i++) {
            Section upStationSection = findUpStationSection(upStation);
            stations.add(upStationSection.getUpStation());
            upStation = upStationSection.getDownStation();
        }
        stations.add(getLineDownStation());
        return Collections.unmodifiableList(stations);
    }

    private Section findUpStationSection(final Station upStation) {
        return getSectionsBy(upStation).stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("역을 상행선으로 하는 구간이 존재하지 않습니다."));
    }

    private List<Section> getSectionsBy(final Station station) {
        return sections.stream()
                .filter(s -> s.isContain(station))
                .collect(Collectors.toUnmodifiableList());
    }

    public Station getLineDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(this::isLineDownStation)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("노선에 하행역이 존재하지 않았습니다."));
    }

    private boolean isLineDownStation(final Station downStation) {
        return sections.stream().filter(section -> section.isUpStation(downStation)).findFirst().isEmpty();
    }

    public Station getLineUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isLineUpStation)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("노선에 상행역이 존재하지 않았습니다."));
    }

    private boolean isLineUpStation(final Station upStation) {
        return sections.stream().filter(section -> section.isDownStation(upStation)).findFirst().isEmpty();
    }
}
