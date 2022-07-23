package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeStation(Station station) {
        if(!sections.get(getLastIndexSections()).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(getLastIndexSections());
    }

    private int getLastIndexSections() {
        return sections.size() - 1;
    }

}
