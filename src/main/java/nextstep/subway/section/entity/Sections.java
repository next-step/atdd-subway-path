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
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Sections {

    @ElementCollection
    @CollectionTable(name = "section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sections = new ArrayList<>();

    private Integer totalDistance = 0;

    public void addSection(Section section) {
        // 아무 구간도 등록되어 있지 않은 상태에서 등록할 경우
        if (sections.isEmpty()) {
            addLastSection(section);
            return;
        }

        List<Station> stations = this.getAllStations();

        // 기준역을 찾는다.
        Station targetStation = findAddableTargetStation(section);

        // 새로운 역을 상행 종점으로 등록하는 경우
        if (stations.indexOf(targetStation) == 0 && section.getDownStation().equals(targetStation)) {
            addFirstLineSection(section);
            return;
        }

        // 새로운 역을 하행 종점으로 등록하는 경우
        if (stations.indexOf(targetStation) == stations.size() - 1 && section.getUpStation().equals(targetStation)) {
            addLastSection(section);
            return;
        }

        // 역 사이에 새로운 역을 등록하는 경우
        addSectionBetweenStations(targetStation, section);
    }

    public void deleteSectionByStationId(Long stationId) {
        List<Long> allStationIds = getAllStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        if (!allStationIds.contains(stationId)) {
            throw new InvalidLineSectionException(ErrorCode.UNREGISTERED_STATION);
        }

        if (sections.size() == 1) {
            throw new InvalidLineSectionException(ErrorCode.STAND_ALONE_LINE_SECTION);
        }

        if (!getLastStation().getId().equals(stationId)) {
            throw new InvalidLineSectionException(ErrorCode.IS_NOT_LAST_LINE_SECTION);
        }

        this.sections = sections.stream()
                .filter(section -> {
                    boolean isDeleteTarget = section.getDownStation().getId().equals(stationId);
                    if (isDeleteTarget) {
                        this.totalDistance -= section.getDistance();
                    }

                    return !isDeleteTarget;
                })
                .collect(Collectors.toList());
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

    /**
     * <pre>
     * 상행 종점역 앞으로 노선을 추가한다.
     * </pre>
     *
     * @param section
     */
    private void addFirstLineSection(Section section) {
        this.sections.add(0, section);
        this.totalDistance += section.getDistance();
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
        Section targetSection = null;

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
                .build();

        sections.set(index, dividedSection);
        sections.add(index + 1, section);
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
     * 하행 종점역 뒤로 노선을 추가한다.
     * </pre>
     *
     * @param section
     */
    private void addLastSection(Section section) {
        this.sections.add(section);
        this.totalDistance += section.getDistance();
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
        if (index == -1) {
            index = stations.indexOf(section.getDownStation());
        }

        // 추가하려는 노선의 상행역, 하행역 전부 지하철 역에 등록되어 있지 않을 경우 예외가 발생한다.
        if (index == -1) {
            throw new InvalidLineSectionException(ErrorCode.UNREGISTERED_STATION);
        }

        return stations.get(index);
    }

    private Station getLastStation() {
        int lastIndex = sections.size() - 1;
        return sections.get(lastIndex).getDownStation();
    }
}
