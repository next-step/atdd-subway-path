package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
>>>>>>> f9a3d00b (refactor: 코드리뷰 반영)

@Embeddable
@Getter
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        addValidate(newSection);

        // 기존 구간의 하행역 기준으로 새로운 구간을 등록
        if (isConnectUpStation(newSection) || isConnectDownStation(newSection)) {
            sections.add(newSection);
            return;
        }

        // 기존 구간 A-C 에 신규 구간 A-B 를 추가하는 경우 검증
        addMiddleStation(newSection);
    }

    private void addValidate(Section newSection) {
        /*
         * 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
         */
        if (isAlreadyRegisterUpStation(newSection) && isAlreadyRegisterDownStation(newSection)) {
            throw new SubwayRuntimeException(SubwayErrorCode.DUPLICATE_SECTION.getMessage());
        }

        /*
         * 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
         */
        if (!(isContainStation(newSection.getUpStation()) || isContainStation(newSection.getDownStation()))) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_CONTAIN_STATION.getMessage());
        }
    }

    // 이미 등록된 상행역인지 검증
    private boolean isAlreadyRegisterUpStation(Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameUpStation(newSection));
    }

    // 이미 등록된 하행역인지 검증
    private boolean isAlreadyRegisterDownStation(Section newSection) {
        return sections.stream().anyMatch(section -> section.isSameDownStation(newSection));
    }

    // 해당 역이 포함되어 있는지 검증
    private boolean isContainStation(Station newSection) {
        return sections.stream()
                .anyMatch(section -> section.isContainStation(newSection));
    }

    // 등록되어 있는 상행역과 새로운 구간의 하행역이 같은지 검증 (구간 연결 [새로운 구간이 앞으로 붙는경우])
    private boolean isConnectDownStation(Section newSection) {
        return getUpStation().equals(newSection.getDownStation());
    }

    // 등록되어 있는 하행역과 새로운 구간의 상행역이 같은지 검증 (구간연결 [새로운 구간이 뒤로 붙는경우])
    private boolean isConnectUpStation(Section newSection) {
        return getDownStation().equals(newSection.getUpStation());
    }

    public Station getUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> sections.stream()
                        .noneMatch(section -> upStation.equals(section.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage()));
    }

    private Station getDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> sections.stream()
                        .noneMatch(section -> downStation.equals(section.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage()));
    }

    private Optional<Station> findNextStationOf(Station prev) {
        return sections.stream()
                .filter(section -> prev.equals(section.getUpStation()))
                .map(Section::getDownStation)
                .findFirst();
    }

    private void addMiddleStation(Section newSection) {
        for (Section section : sections) {
            if (addStation(section, newSection)) return;
        }
    }

    private boolean addStation(Section section, Section newSection) {
        return updateDownStation(section, newSection) || updateUpStation(section, newSection);
    }

    private boolean updateDownStation(Section section, Section newSection) {
        if (section.getUpStation().equals(newSection.getUpStation())) {
            sections.add(section.add(newSection.getDownStation(), section.getDistance() - newSection.getDistance()));
            return true;
        }

        return false;
    }

    private boolean updateUpStation(Section section, Section newSection) {
        if (section.getDownStation().equals(newSection.getDownStation())) {
            sections.add(section.add(newSection.getUpStation(), newSection.getDistance()));
            return true;
        }

        return false;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if (sections.isEmpty()) {
            return stations;
        }
        Station prev = getUpStation();
        while (findNextStationOf(prev).isPresent()) {
            stations.add(prev);
            prev = findNextStationOf(prev).get();
        }
        stations.add(prev);

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void delete(Long stationId) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new SubwayRuntimeException(SubwayErrorCode.CANNOT_DELETE_STATION.getMessage());
        }

        List<Section> deleteSections = findSectionByStationId(stationId);

        if (deleteSections.size() == 2) {
            Section mergedSection = merge(deleteSections.get(0), deleteSections.get(1));
            sections.add(mergedSection);
        }
        sections.removeAll(deleteSections);
    }

    private Section merge(Section prevSection, Section nextSection) {
        return prevSection.merge(prevSection, nextSection);
    }

    private List<Section> findSectionByStationId(long stationId) {
        List<Section> deleteSections = sortedSections().stream()
                // 삭제할 역이 포함된 구간을 찾음
                .filter(it -> it.hasStationId(stationId))
                .collect(Collectors.toList());

        if (deleteSections.isEmpty()) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage());
        }

        return deleteSections;
    }

    private List<Section> sortedSections() {
        List<Section> sortedSections = new ArrayList<>();
        // 상행역이 없는 역을 찾는다.
        findUpEndSection().ifPresent(upEndSection -> {
            Section nextSection = upEndSection;
            // 상행역이 없는 역부터 하행역이 없는 역까지 순서대로 정렬한다.
            while (nextSection != null) {
                // 정렬된 역 목록에 추가한다.
                sortedSections.add(nextSection);
                // 다음 역을 찾는다.
                nextSection = nextOf(nextSection);
            }
        });

        return sortedSections;
    }

    private Optional<Section> findUpEndSection() {
        // 상행 종점 찾기
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                // 상행역이 하행역 목록에 없는 구간을 찾는다.
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst();
    }

    private Section nextOf(Section section) {
        return sections.stream()
                // 다음 구간의 상행역이 현재 구간의 하행역과 같은 구간을 찾는다.
                .filter(it -> it.getUpStation() == section.getDownStation())
                // 찾은 구간이 없으면 null을 반환한다.
                .findFirst()
                .orElse(null);
    }

    public int getDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
