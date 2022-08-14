package nextstep.subway.domain;


import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

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

    public void remove() {
        int lastSectionIndex = sections.size() - 1;
        if (lastSectionIndex > 0) {
            sections.remove(lastSectionIndex);
            return;
        }
        throw new IllegalArgumentException("구간이 2개 이상인 경우에만 삭제가 가능합니다.");
    }

    // 상행종점역이 포함된 구간 조회
    private Section getExistedSectionWithLastUpStation() {
        if (sections.size() == 1) {
            return sections.get(0);
        }
        Section sectionWithLastUpStation = null;
        for (Section section : sections) {
            boolean isSectionWithLastUpStation = sections.stream()
                    .noneMatch((comparisonSection) -> comparisonSection.getDownStation().equals(section.getUpStation()));
            if (isSectionWithLastUpStation) {
                sectionWithLastUpStation = section;
                break;
            }
        }
        return sectionWithLastUpStation;
    }

    // 하행종점역이 포함된 구간 조회
    private Section getExistedSectionWithLastDownStation() {
        if (sections.size() == 1) {
            return sections.get(0);
        }
        Section sectionWithLastDownStation = null;
        for (Section section : sections) {
            boolean isSectionWithLastDownStation = sections.stream()
                    .noneMatch((comparisonSection) -> comparisonSection.getUpStation().equals(section.getDownStation()));
            if (isSectionWithLastDownStation) {
                sectionWithLastDownStation = section;
                break;
            }
        }
        return sectionWithLastDownStation;
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

    private boolean notFoundUpAndDownStations(Section newSection) {
        return getStations().stream().noneMatch((station) -> station.equals(newSection.getDownStation()) ||
                                                            station.equals(newSection.getUpStation()));
    }

    public Section findSectionMatchingUpStation(Station upStation) {
        return sections.stream().filter((section) -> section.getUpStation().equals(upStation)).findFirst().orElse(null);
    }
}
