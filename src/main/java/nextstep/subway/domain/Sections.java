package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void remove() {
        int lastSectionIndex = sections.size() - 1;
        if (lastSectionIndex > 0) {
            sections.remove(lastSectionIndex);
            return;
        }
        throw new IllegalArgumentException("구간이 2개 이상인 경우에만 삭제가 가능합니다.");
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        stations.add(sections.get(0).getUpStation());
        for (Section section: sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        // 같은 구간이면 안 됨
        if (isSameSection(newSection)) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
        // 예외처리가 들어갈 부분 (종점 요구사항에 대한)

        // 새로 추가하려는 구간의 상행역과 매칭되는 기존 구간을 찾는다
        Section existedSection = findSectionMatchingUpStation(newSection.getUpStation());

        if (existedSection == null) {
            throw new IllegalArgumentException("추가하려는 새로운 구간과 매칭되는 기존 역을 찾을 수 없습니다.");
        }

        // 추가하려는 구간이 기존 매칭되는 구간보다 거리가 더 짧은지 체크
        if (newSection.getDistance() >= existedSection.getDistance()) {
            throw new IllegalArgumentException("기존 구간보다 거리가 더 긴 구간은 등록할 수 없습니다.");
        }

        // 새로 구간 추가
        sections.add(newSection);

        int updatedDistance = existedSection.getDistance() - newSection.getDistance();
        // 기존 구간의 상행역은 새로 추가한 구간의 하행역으로 업데이트 및 거리 수정
        existedSection.updateUpStation(newSection.getDownStation(), updatedDistance);
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


    public boolean isSameSection(Section newSection) {
        return sections.stream().anyMatch((section) ->
                section.getUpStation().equals(newSection.getUpStation()) &&
                section.getDownStation().equals(newSection.getDownStation()));
    }

    public Section findSectionMatchingUpStation(Station upStation) {
        return sections.stream().filter((section) -> section.getUpStation().equals(upStation)).findFirst().orElse(null);
    }
}
