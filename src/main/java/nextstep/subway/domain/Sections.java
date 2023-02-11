package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    public static final String EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED = "지하철노선은 1개 구간 이하로 구성될 수 없습니다.";
    public static final String EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION = "해당 노선의 하행종점역만 제거할 수 있습니다.";
    public static final String EXCEPTION_MESSAGE_NEED_ONE_CRITERIA_STATION = "요청한 구간의 모든 역 중 노선에 존재하는 노선이 없어서 구간을 추가할 수 없습니다.";
    public static final String EXCEPTION_MESSAGE_ALL_REQUEST_STATIONS_ALREADY_RESISTER = "요청한 구간의 모든 역은 이미 노선에 존재하여 구간을 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("id asc")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validateAddableSection(section);

        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        InsertLocation insertLocation = findInsertLocation(section);
        if (!insertLocation.isBetween()) {
            sections.add(section);
            return;
        }
        addBetweenSection(insertLocation, section);
    }

    private void addBetweenSection(InsertLocation insertLocation, Section section) {
        Section criteriaSection = null;
        Section foreSection = null;
        Section rearSection = null;

        if (insertLocation.isNextHead()) {
            criteriaSection = findSectionOnUpStationOfSection(section);
            foreSection = new Section(criteriaSection.getLine(), criteriaSection.getUpStation(), section.getDownStation(), section.getDistance());
            rearSection = new Section(criteriaSection.getLine(), section.getDownStation(), criteriaSection.getDownStation(), criteriaSection.getDistance() - section.getDistance());
        }
        if (insertLocation.isPrevTail()) {
            criteriaSection = findSectionOnDownStationOfSection(section);
            foreSection = new Section(criteriaSection.getLine(), criteriaSection.getUpStation(), section.getUpStation(), criteriaSection.getDistance() - section.getDistance());
            rearSection = new Section(criteriaSection.getLine(), section.getUpStation(), criteriaSection.getDownStation(), section.getDistance());
        }

        sections.remove(criteriaSection);
        sections.add(foreSection);
        sections.add(rearSection);
    }

    private Section findSectionOnUpStationOfSection(Section target) {
        return sections.stream()
                .filter(section -> section.isInSideOverlapOnUpStation(target))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section findSectionOnDownStationOfSection(Section target) {
        return sections.stream()
                .filter(section -> section.isInSideOverlapOnDownStation(target))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


    private InsertLocation findInsertLocation(Section target) {
        for (int index = 0; index < sections.size(); index++) {
            Section section = sections.get(index);

            if (section.isOutSideOverlapOnUpStation(target)) {
                return InsertLocation.PREV_HEAD;
            }

            if (section.isOutSideOverlapOnDownStation(target)) {
                return InsertLocation.NEXT_TAIL;
            }

            if (section.isInSideOverlapOnUpStation(target)) {
                return InsertLocation.NEXT_HEAD;
            }
            if (section.isInSideOverlapOnDownStation(target)) {
                return InsertLocation.PREV_TAIL;
            }
        }

        throw new IllegalArgumentException();
    }

    private void validateAddableSection(Section section) {
        verifyExistenceCriteriaStation(section);
        verifyOnlyOneCriteriaStationExistence(section);
    }

    private void verifyExistenceCriteriaStation(Section section) {
        if (!sections.isEmpty() && !hasMatchedStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_NEED_ONE_CRITERIA_STATION);
        }
    }

    private void verifyOnlyOneCriteriaStationExistence(Section section) {
        if (hasAllMatchedStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_ALL_REQUEST_STATIONS_ALREADY_RESISTER);
        }
    }

    private boolean hasMatchedStation(Section section) {
        return hasStation(section.getUpStation())
                || hasStation(section.getDownStation());
    }

    private boolean hasAllMatchedStation(Section section) {
        return hasStation(section.getUpStation())
                && hasStation(section.getDownStation());
    }

    private boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasStation(station));
    }

    public void remove(Station station) {
        checkRemovableStation(station);

        sections.remove(sections.size() - 1);
    }

    private void checkRemovableStation(Station station) {
        verifyMinimumSectionCount();
        verifyTailStation(station);
    }

    private void verifyMinimumSectionCount() {
        if (sections.size() == 1) {
            throw new IllegalStateException(EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED);
        }
    }

    private void verifyTailStation(Station station) {
        Station tailStation = getTailStation();
        if (!tailStation.equals(station)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION);
        }
    }

    private Station getTailStation() {
        return getTailSection().getDownStation();
    }

    private Section getTailSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        return getSequentialStations();
    }

    private List<Station> getSequentialStations() {
        return makeSequentialStations(getHeadSection());
    }

    private List<Station> makeSequentialStations(Section headSection) {
        List<Station> stations = new ArrayList<>();

        stations.add(headSection.getUpStation());
        addFollowingStations(headSection, stations);

        return stations;
    }

    private void addFollowingStations(Section headSection, List<Station> stations) {
        Station downStation = headSection.getDownStation();
        while (true) {
            stations.add(downStation);

            Optional<Station> nextDownStation = findNextStation(downStation);

            if (nextDownStation.isEmpty()) {
                break;
            }
            downStation = nextDownStation.get();
        }
    }

    public Section getHeadSection() {
        Section section = sections.get(0);

        while (true) {
            Optional<Section> preSection = findPreSection(section);

            if (preSection.isEmpty()) {
                break;
            }
            section = preSection.get();
        }

        return section;
    }

    private Optional<Section> findPreSection(Section target) {
        return sections.stream()
                .filter(section -> section.isDownStation(target.getUpStation()))
                .findAny();
    }

    private Optional<Station> findNextStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(downStation))
                .map(Section::getDownStation)
                .findAny();
    }

    public int size() {
        return sections.size();
    }
}
