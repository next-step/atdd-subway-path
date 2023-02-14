package nextstep.subway.domain;

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
     *
     * @param section 등록할 지하철 구간 정보
     */
    public void add(final Section section) {
        sectionList.add(section);
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
     * 구간이 이미 등록되어있는 경우에만 true를 반환합니다.
     *
     * @param section 비교할 구간
     * @return 구간이 이미 등록되어 있으면 true, 그렇지 않으면 false
     */
    public boolean isAlreadyRegisteredSection(final Section section) {
        return sectionList.stream()
                .anyMatch(sec -> sec.equals(section));
    }

    /**
     * 구간의 길이가 0인 경우에만 true 반환합니다.
     *
     * @return 구간의 길이가 0이면 true, 그렇지 않으면 false
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

    // 해당 구간이 상행 종점 구간인지? -> 해당 구간의 상행역은 다른 구간들의 하행역이 아니다
    private boolean isFinalUpStation(final Section section) {
        return sectionList.stream()
                .noneMatch(sec -> sec.getDownStation().equals(section.getUpStation()));
    }

    // 1. 상행 종점이 상행역인 구간을 먼저 찾는다.
    private void addFirstSection(List<Station> stations) {
        for (Section section : sectionList) {
            if (isFinalUpStation(section)) {
                stations.add(section.getUpStation());
                stations.add(section.getDownStation());
                break;
            }
        }
    }

    // 2. 그 다음 해당 구간의 하행역이 상행역인 다른 구간을 찾는다. (하행 종점역까지 -> 즉 끝까지)
    private void addAllSectionExceptFirst(List<Station> stations) {
        for (int i = 1; i < sectionList.size(); i++) {
            Section section = sectionList.stream()
                    .filter(sec -> sec.getUpStation().equals(stations.get(stations.size() - 1)))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);

            stations.add(section.getDownStation());
        }
    }
}
