package nextstep.subway.section.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.station.entity.Station;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Sections {

    @ElementCollection
    @CollectionTable(name = "section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sections = new ArrayList<>();

    private static final int INVALID_INDEX = -1;

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            addLastSection(section);
            return;
        }

        Station targetStation = findAddableTargetStation(section);

        if (isAddableFirstSection(section)) {
            addFirstSection(section);
            return;
        }

        if (isAddableLastSection(section)) {
            addLastSection(section);
            return;
        }

        addSectionBetweenStations(targetStation, section);
    }

    public void deleteSectionByStationId(Long stationId) {
        if (!containsStation(stationId)) {
            throw new InvalidLineSectionException(ErrorCode.UNREGISTERED_STATION);
        }

        if (sections.size() == 1) {
            throw new InvalidLineSectionException(ErrorCode.STAND_ALONE_LINE_SECTION);
        }

        if (isUpStation(stationId)) {
            deleteUpStation(stationId);
            return;
        }

        if (isDownStation(stationId)) {
            deleteDownStation(stationId);
            return;
        }

        deleteMiddleStation(stationId);
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = this.sections
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        int lastIndex = sections.size() - 1;
        stations.add(sections.get(lastIndex).getDownStation());

        return stations;
    }

    public int getTotalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private boolean isAddableFirstSection(Section section) {
        List<Station> stations = this.getAllStations();
        Station targetStation = findAddableTargetStation(section);
        return stations.indexOf(targetStation) == 0 && section.getDownStation().equals(targetStation);
    }

    /**
     * <pre>
     * 상행 종점역 앞으로 노선을 추가한다.
     * </pre>
     *
     * @param section
     */
    private void addFirstSection(Section section) {
        this.sections.add(0, section);
    }

    /**
     * <pre>
     * 역 사이에 등록할 새로운 노선에 대한 검증
     * </pre>
     *
     * @param accumulator
     * @param currentValue
     * @throws InvalidLineSectionException
     */
    private void validateAddSectionBetweenStations(Section accumulator, Section currentValue) {
        if (accumulator.getDownStation().equals(currentValue.getDownStation()) &&
                accumulator.getUpStation().equals(currentValue.getUpStation())) {
            throw new InvalidLineSectionException(ErrorCode.ALREADY_REGISTERED_SECTION);
        }

        if (accumulator.getDistance() <= currentValue.getDistance()) {
            throw new InvalidLineSectionException(ErrorCode.INVALID_DISTANCE);
        }
    }

    /**
     * <pre>
     * 역 사이에 새로운 노선을 등록한다.
     * </pre>
     *
     * @param targetStation
     * @param section
     */
    private void addSectionBetweenStations(Station targetStation, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Section targetSection;

        // 역 사이에 상행역 기준으로 새로운 노선을 등록한다.
        if (targetStation.equals(section.getUpStation())) {
            targetSection = sections.stream()
                    .filter(s -> s.getUpStation().equals(upStation))
                    .findFirst().get();

            this.validateAddSectionBetweenStations(targetSection, section);

            int index = sections.indexOf(targetSection);
            Section dividedSection = Section.builder()
                    .upStation(section.getDownStation())
                    .downStation(targetSection.getDownStation())
                    .distance(targetSection.getDistance() - section.getDistance())
                    .build();

            sections.set(index, section);
            sections.add(index + 1, dividedSection);

            return;
        }

        // 역 사이에 하행역 기준으로 새로운 노선을 등록한다.
        targetSection = sections.stream()
                .filter(s -> s.getUpStation().equals(downStation))
                .findFirst().get();

        this.validateAddSectionBetweenStations(targetSection, section);

        int index = sections.indexOf(targetSection);
        Section dividedSection = Section.builder()
                .upStation(targetSection.getUpStation())
                .downStation(section.getUpStation())
                .distance(targetSection.getDistance() - section.getDistance())
                .build();

        sections.set(index, dividedSection);
        sections.add(index + 1, section);
    }

    private boolean isAddableLastSection(Section section) {
        List<Station> stations = this.getAllStations();
        Station targetStation = findAddableTargetStation(section);
        return stations.indexOf(targetStation) == stations.size() - 1 && section.getUpStation().equals(targetStation);
    }

    /**
     * <pre>
     * 하행 종점역 뒤로 노선을 추가한다.
     * </pre>
     *
     * @param section
     */
    private void addLastSection(Section section) {
        this.sections.add(section);
    }

    /**
     * <pre>
     * 구간을 더할 때 기준이 되는 역을 찾는다.
     * </pre>
     *
     * @param section
     * @return target station
     * @throws InvalidLineSectionException
     */
    private Station findAddableTargetStation(Section section) {
        List<Station> stations = this.getAllStations();
        int index = stations.indexOf(section.getUpStation());
        if (index == INVALID_INDEX) {
            index = stations.indexOf(section.getDownStation());
        }

        // 추가하려는 노선의 상행역, 하행역 전부 지하철 역에 등록되어 있지 않을 경우 예외가 발생한다.
        if (index == INVALID_INDEX) {
            throw new InvalidLineSectionException(ErrorCode.UNREGISTERED_STATION);
        }

        return stations.get(index);
    }

    private boolean containsStation(Long stationId) {
        List<Long> allStationIds = getAllStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        return allStationIds.contains(stationId);
    }

    private boolean isUpStation(Long stationId) {
        return sections
                .get(0)
                .getUpStation()
                .getId()
                .equals(stationId);
    }

    /**
     * <pre>
     * 상행 종점역을 삭제한다.
     * </pre>
     *
     * @param stationId
     */
    private void deleteUpStation(Long stationId) {
        this.sections = sections.stream()
                .filter(section -> !section.getUpStation().getId().equals(stationId))
                .collect(Collectors.toList());
    }

    /**
     * <pre>
     * 중간역을 삭제한다.
     * </pre>
     *
     * @param stationId
     */
    private void deleteMiddleStation(Long stationId) {
        List<Section> targetSections = sections.stream()
                .filter(section ->
                        section.getUpStation().getId().equals(stationId) ||
                        section.getDownStation().getId().equals(stationId))
                .collect(Collectors.toList());
        int index = sections.indexOf(targetSections.get(0));

        Section mergedSection = Section.builder()
                .upStation(targetSections.get(0).getUpStation())
                .downStation(targetSections.get(1).getDownStation())
                .distance(targetSections.stream()
                        .mapToInt(Section::getDistance)
                        .sum())
                .build();

        this.sections.set(index, mergedSection);
        this.sections.remove(index + 1);
    }

    private boolean isDownStation(Long stationId) {
        int lastIndex = sections.size() - 1;
        return sections
                .get(lastIndex)
                .getDownStation()
                .getId()
                .equals(stationId);
    }

    /**
     * <pre>
     * 하행 종점역을 삭제한다.
     * </pre>
     *
     * @param stationId
     */
    private void deleteDownStation(Long stationId) {
        this.sections = sections.stream()
                .filter(section -> !section.getDownStation().getId().equals(stationId))
                .collect(Collectors.toList());
    }
}
