package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        sections.add(section);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean isEqualLastSectionDownStation(Station station){
        return getLastSection().getDownStation().equals(station);
    }

    public void remove() {
        sections.remove(getLastSection());
    }

    private Section getLastSection() {
        return sections.stream()
                .reduce((prev, next) -> next)
                .orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."));
    }
}
