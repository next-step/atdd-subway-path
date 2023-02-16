package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Getter;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(s -> Arrays.asList(s.getUpStation(), s.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void remove(Station station) {
        int lastIndex = sections.size() - 1;
        if (!sections.get(lastIndex).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastIndex);
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
