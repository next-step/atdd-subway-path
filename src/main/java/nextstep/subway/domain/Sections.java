package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        Section lastSection = sections.get(sections.size() - 1);

        if (getStations().contains(section.getDownStation())) {
            throw new IllegalStateException("하행선이 이미 추가 되어 있습니다.");
        }

        if (!lastSection.getDownStation().equals(section.getUpStation())) {
            throw new IllegalStateException("Section에 추가할 수 있는 Station이 없습니다.");
        }

        sections.add(section);
    }

    public void deleteSection(Station downStation) {
        if (sections.stream().count() <= 1) {
            throw new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다.");
        }
        Section lastSection = getLastSection();
        if (!lastSection.getDownStation().equals(downStation)) {
            throw new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다.");
        }
        getStations().stream().filter(station -> station.equals(downStation)).findAny().ifPresent(x -> new IllegalStateException("Section에 삭제할 수 있는 Station이 없습니다."));
        sections.remove(sections.size()-1);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stationList = new ArrayList<>();
        stationList.add(sections.get(0).getUpStation());
        sections.forEach(section -> stationList.add(section.getDownStation()));

        return stationList;
    }

    public Section getLastSection() {
        if (sections.size() <= 1) {
            throw new IllegalStateException("마지막 section이 존재하지 않습니다.");
        }
        return sections.get(sections.size() - 1);
    }
}
