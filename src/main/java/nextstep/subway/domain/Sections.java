package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        Section downEndStation = getDownEndStation();
        if (!downEndStation.isMatched(section)) {
            throw new IllegalArgumentException("새로운 구간 등록시 새로운 구간의 상행역은 기등록된 하행 종점역과 같아야 합니다.");
        }

        if (stationsContain(section.getDownStation())) {
            throw new IllegalArgumentException("신규 구간의 하행역은 현재 등록되어있는 역일 수 없습니다.");
        }
        sections.add(section);
    }

    private boolean stationsContain(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    private Section getDownEndStation() {
        int downEndStationIndex = getDownEndStationIndex();
        return sections.get(downEndStationIndex);
    }

    private int getDownEndStationIndex() {
        return sections.size() - 1;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if (sections.isEmpty()) {
            return stations;
        }

        stations.add(sections.get(0).getUpStation());

        for (Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public void deleteSection(Station station) {
        Section downEndStation = getDownEndStation();
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("지하철 노선이 상행 종점역과 하행 종점역만 있는 하나의 구간인 경우, 역을 삭제할 수 없습니다.");
        }

        if (!downEndStation.isMatchedStationId(station)) {
            throw new IllegalArgumentException("지하철 노선의 하행 종점역만 삭제 할 수 있습니다.");
        }
        removeDownEndSection();
    }

    private void removeDownEndSection() {
        sections.remove(getDownEndStationIndex());
    }
}
