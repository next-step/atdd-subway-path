package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Station> getAllStations() {
        return this.sections
                .stream()
                .flatMap(section -> section.getAllStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteSection(Station station) {
        final int lastIndex = this.sections.size() - 1;
        final Section lastSection = this.sections.get(lastIndex);
        if (!lastSection.isDownStation(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(lastIndex);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public int size() {
        return this.sections.size();
    }
}
