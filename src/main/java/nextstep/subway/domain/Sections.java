package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int SECTION_MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }


    public void remove(Long stationId) {
        if (sections.size() <= SECTION_MIN_SIZE) {
            throw new IllegalArgumentException("상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없습니다.");
        }

        if (!getLastSection().getDownStation().getId().equals(stationId)) {
            throw new IllegalArgumentException("지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있습니다.");
        }

        sections.remove(getLastSection());
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        stations.addAll(
            sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList())
        );
        return stations;
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}