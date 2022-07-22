package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return this.sections;
    }

    public void add(Section section) {
        if (!sections.isEmpty() && !isFinalDownStation(section.getUpStation())) {
            throw new IllegalArgumentException();
        }

        this.sections.add(section);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteSection(Station station) {
        if (!isFinalDownStation(station)) {
            throw new IllegalArgumentException();
        }

        this.getSections().remove(this.sections.size() - 1);
    }

    private boolean isFinalDownStation(Station station) {
        return this.sections.get(this.sections.size() - 1).getDownStation().equals(station);
    }
}
