package nextstep.subway.domain;

import nextstep.subway.exception.NotFoundSectionException;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Section findSection(Station upStation, Station downStation) {
        return sections.stream()
                .filter((section) -> section.matchStations(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 상행역과 하행역으로 조회되는 구간이 없습니다."));
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        isValidSection(newSection);

        if (isNewLastDownStation(newSection)) {
            sections.add(newSection);
            return;
        }

        if (isNewLastUpStation(newSection)) {
            sections.add(newSection);
            return;
        }

        addNewStationInExistedSection(newSection);
    }

    // 새로운 역을 기존 구간의 중간에 추가
    private void addNewStationInExistedSection(Section newSection) {
        Section existedSection = findSectionMatchingUpStation(newSection.getUpStation());
        if (newSection.getDistance() >= existedSection.getDistance()) {
            throw new IllegalArgumentException("기존 구간보다 거리가 더 긴 구간은 등록할 수 없습니다.");
        }
        sections.add(newSection);
        // 기존 구간의 상행역은 새로 추가한 구간의 하행역으로 업데이트 및 거리 재조정
        existedSection.updateUpStation(newSection.getDownStation(), existedSection.getDistance() - newSection.getDistance());
    }

    // 새로운 역을 하행 종점으로 등록하는 경우
    private boolean isNewLastDownStation(Section newSection) {
        return getExistedSectionWithLastDownStation().getDownStation().equals(newSection.getUpStation());
    }

    // 새로운 역을 상행 종점으로 등록하는 경우
    private boolean isNewLastUpStation(Section newSection) {
        return getExistedSectionWithLastUpStation().getUpStation().equals(newSection.getDownStation());
    }

    // 새로 추가하려는 구간 유효성 검사
    private void isValidSection(Section newSection) {
        if (isAlreadyRegistered(newSection)) {
            throw new IllegalArgumentException("이미 노선에 등록된 구간입니다.");
        }

        if (notFoundUpAndDownStations(newSection)) {
            throw new IllegalArgumentException("추가하려는 구간의 상행역 및 하행역이 기존 구간에 존재하지 않습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        // 상행종점역이 포함된 구간 조회
        Section sectionWithLastUpStation = getExistedSectionWithLastUpStation();

        // 상행종점역 추가 후 하행종점역까지 구간 찾아가면서 지하철역 목록에 추가
        stations.add(sectionWithLastUpStation.getUpStation());
        addExtraStations(stations, sectionWithLastUpStation);

        return stations;
    }

    public void remove(Station station) {
        removeValidationCheck(station);

        Section sectionWithLastUpStation = getExistedSectionWithLastUpStation();
        if (sectionWithLastUpStation.matchUpStation(station)) {
            sections.remove(sectionWithLastUpStation);
            return;
        }

        Section sectionWithLastDownStation = getExistedSectionWithLastDownStation();
        if (sectionWithLastDownStation.matchDownStation(station)) {
            sections.remove(sectionWithLastDownStation);
            return;
        }

        removeMiddleStation(station);
    }

    private void removeMiddleStation(Station station) {
        Section sectionMatchingUpStation = findSectionMatchingUpStation(station);
        Section sectionMatchingDownStation = findSectionMatchingDownStation(station);

        Station newDownStation = sectionMatchingUpStation.getDownStation();
        int newDistance = sectionMatchingUpStation.getDistance() + sectionMatchingDownStation.getDistance();

        sections.remove(sectionMatchingUpStation);
        // 역 삭제 후 구간 재배치
        sectionMatchingDownStation.updateDownStation(newDownStation, newDistance);
    }

    private void removeValidationCheck(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("해당 노선에 존재하지 않는 역은 제거할 수 없습니다.");
        }
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("구간이 2개 이상 존재하는 노선에서만 역을 제거할 수 있습니다.");
        }
    }

    // 상행종점역이 포함된 구간 조회
    private Section getExistedSectionWithLastUpStation() {
        return findExistedSectionWithLastUpStation(0);
    }

    private Section findExistedSectionWithLastUpStation(int index) {
        Section section = sections.get(index);
        if (section.isSectionWithLastUpStation(sections)) {
            return section;
        }
        return findExistedSectionWithLastUpStation(index + 1);
    }


    // 상행종점역이 포함된 구간 조회
    private Section getExistedSectionWithLastUpStation2() {
        return getLastStation((section) -> section.isSectionWithLastDownStation(sections));
    }

    // 하행종점역이 포함된 구간 조회
    private Section getExistedSectionWithLastDownStation() {
        return getLastStation((section) -> section.isSectionWithLastDownStation(sections));

    }

    private Section getLastStation(Predicate<Section> lastStationCheck) {
        return sections.stream()
                .filter(lastStationCheck)
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    // 하행종점역까지 남아있는 역을 목록에 추가
    public void addExtraStations(List<Station> stations, Section section) {
        Section nextSection = sections.stream().filter((comparisonSection) -> comparisonSection.getUpStation().equals(section.getDownStation()))
                .findFirst().orElse(null);

        // 하행종점역인 경우 하행역 추가 후 메서드 종료
        if (nextSection == null) {
            stations.add(section.getDownStation());
            return;
        }

        // 하행종점역이 아닌 경우 상행역 추가 후 메서드 재실행
        stations.add(nextSection.getUpStation());
        addExtraStations(stations, nextSection);
    }

    public int count() {
        return sections.size();
    }

    public Station getFirstUpStation() {
        return sections.get(0).getUpStation();
    }

    public Station getLastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public boolean isAlreadyRegistered(Section newSection) {
        List<Station> newSectionUpAndDownStation = new ArrayList<>();
        newSectionUpAndDownStation.add(newSection.getUpStation());
        newSectionUpAndDownStation.add(newSection.getDownStation());

        return getStations().containsAll(newSectionUpAndDownStation);
    }

    private boolean notFoundUpAndDownStations(Section section) {
        return getStations().stream()
                .noneMatch((station) -> station.equals(section.getDownStation())
                                    || station.equals(section.getUpStation()));
    }

    public Section findSectionMatchingUpStation(Station station) {
        return sections.stream()
                .filter((section) -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionMatchingDownStation(Station station) {
        return sections.stream()
                .filter((section) -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(null);
    }

    public List<Section> getSections() {
        return sections;
    }
}
