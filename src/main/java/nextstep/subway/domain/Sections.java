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
        this.sectionList.add(section);
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
        return this.sectionList.stream()
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
     * 지하철 노선에 포함된 구간들의 역 목록을 조회합니다.
     *
     * @return 지하철 노선에 포함된 역 목록 반환
     */
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        sectionList.forEach(section -> {
            stations.add(section.getUpStation());
        });

        if (!sectionList.isEmpty()) {
            stations.add(sectionList.get(sectionList.size() - 1).getDownStation());
        }

        return stations;
    }


    private Section getLastSection() {
        if (isEmpty()) {
            throw new IllegalArgumentException();
        }

        return sectionList.get(sectionList.size() - 1);
    }
}
