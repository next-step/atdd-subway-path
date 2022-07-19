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

    public Sections(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        Section downEndStation = getDownEndStation();
        if (!downEndStation.isMatched(section)) {
            throw new IllegalArgumentException("새로운 구간 등록시 새로운 구간의 상행역은 기등록된 하행 종점역과 같아야 합니다.");
        }

        if(stationsContain(section.getDownStation())){
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
}
