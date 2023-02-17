package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NotRegisteredUpStationAndDownStationException;
import nextstep.subway.exception.SectionAlreadyRegisteredException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sectionList = new ArrayList<>();

    /**
     * 지하철 노선의 구간을 등록합니다.
     * getter를 제공하지 않음으로써 예외적인 구간 등록을 방지합니다.
     *
     * @param section 등록할 지하철 구간 정보
     */
    public void add(final Section section) {

        if (sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }

        if (isAlreadyRegisteredSection(section)) {
            throw new SectionAlreadyRegisteredException();
        }

        List<Station> stations = getAllStations();

        if (isNotRegisteredUpStationAndDownStation(stations, section)) {
            throw new NotRegisteredUpStationAndDownStationException();
        }

        if (isTerminalUpStation(stations, section) || isTerminalDownStation(stations, section)) {
            sectionList.add(section);
            return;
        }

        addBetweenExistingSections(section);
    }

    /**
     * 지하철 노선의 마지막 구간을 삭제합니다.
     *
     * @param station 삭제할 지하철 구간의 역 정보
     */
    public void removeLastSection(final Station station) {
        Section lastSection = getLastSection();

        if (!lastSection.getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sectionList.remove(lastSection);
    }

    /**
     * 구간의 길이가 0인 경우에만 true 반환합니다.
     *
     * @return 구간 목록의 길이가 0이면 true, 그렇지 않으면 false
     */
    public boolean isEmpty() {
        return sectionList.isEmpty();
    }

    /**
     * 지하철 노선에 포함된 구간들의 역 목록을 상행 종점역 기준으로 정렬하여 조회합니다.
     *
     * @return 지하철 노선에 포함된 졍렬된 역 목록 반환
     */
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        addFirstSection(stations);
        addAllSectionExceptFirst(stations);

        return stations;
    }


    private Section getLastSection() {
        if (isEmpty()) {
            throw new IllegalArgumentException();
        }

        return sectionList.get(sectionList.size() - 1);
    }

    private boolean isAlreadyRegisteredSection(final Section section) {
        return sectionList.stream()
                .anyMatch(sec -> sec.getUpStation().equals(section.getUpStation())
                        && sec.getDownStation().equals(section.getDownStation()));
    }

    // 해당 구간이 상행 종점 구간인지? -> 해당 구간의 상행역은 다른 구간들의 하행역이 아니다
    private boolean isFinalUpStation(final Section section) {
        return sectionList.stream()
                .noneMatch(sec -> sec.getDownStation().equals(section.getUpStation()));
    }

    // 1. 상행 종점이 상행역인 구간을 먼저 찾는다.
    private void addFirstSection(final List<Station> stations) {
        for (Section section : sectionList) {
            if (isFinalUpStation(section)) {
                stations.add(section.getUpStation());
                stations.add(section.getDownStation());
                return;
            }
        }
    }

    // 2. 그 다음 해당 구간의 하행역이 상행역인 다른 구간을 찾는다. (하행 종점역까지 -> 즉 끝까지)
    private void addAllSectionExceptFirst(final List<Station> stations) {
        for (int i = 1; i < sectionList.size(); i++) {
            sectionList.stream()
                    .filter(sec -> sec.getUpStation().equals(stations.get(stations.size() - 1)))
                    .findFirst()
                    .ifPresent(sec -> stations.add(sec.getDownStation()));
        }
    }

    private boolean isNotRegisteredUpStationAndDownStation(List<Station> stations, final Section section) {
        return stations.stream()
                .noneMatch(station -> station.equals(section.getUpStation()))
                && stations.stream()
                .noneMatch(station -> station.equals(section.getDownStation()));
    }

    private boolean isTerminalUpStation(final List<Station> stations, final Section section) {
        return stations.get(0).equals(section.getDownStation());
    }

    private boolean isTerminalDownStation(final List<Station> stations, final Section section) {
        return stations.get(stations.size() - 1).equals(section.getUpStation());
    }

    // 1. 신규 구간의 상행역이 기존 상행역이라면 -> 상행역 기준으로 뒤로 끼워넣기 (A-C -> A-B = A-B-C)
    // 2. 1번이 성립되지 않고, 신규 구간의 하행역이 기존 하행역이라면 -> 하행역 기준으로 앞으로 끼워넣기 (A-C -> B-C = A-B-C)
    private void addBetweenExistingSections(final Section section) {
        if (isAlreadyRegisteredUpStation(section.getUpStation())) {
            addAfterUpStation(section);
            return;
        }

        if (isAlreadyRegisteredDownStation(section.getDownStation())) {
            addBeforeDownStation(section);
        }
    }

    private boolean isAlreadyRegisteredUpStation(final Station station) {
        return sectionList.stream()
                .anyMatch(sec -> sec.getUpStation().equals(station));
    }

    private boolean isAlreadyRegisteredDownStation(final Station station) {
        return sectionList.stream()
                .anyMatch(sec -> sec.getDownStation().equals(station));
    }

    private void addAfterUpStation(final Section section) {
        sectionList.stream()
                .filter(sec -> sec.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(
                        originSection -> {
                            if (section.getDistance() >= originSection.getDistance()) {
                                throw new InvalidDistanceException();
                            }

                            originSection.updateUpStation(
                                    section.getDownStation(),
                                    originSection.getDistance() - section.getDistance()
                            );

                            sectionList.add(section);
                        }
                );
    }

    private void addBeforeDownStation(final Section section) {
        sectionList.stream()
                .filter(sec -> sec.getDownStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(originSection -> {
                    if (section.getDistance() >= originSection.getDistance()) {
                        throw new InvalidDistanceException();
                    }

                    originSection.updateDownStation(
                            section.getUpStation(),
                            originSection.getDistance() - section.getDistance()
                    );

                    sectionList.add(section);
                });
    }
}
