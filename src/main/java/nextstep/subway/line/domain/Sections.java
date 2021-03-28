package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section ...sections) {
        this.sections.addAll(Arrays.asList(sections));
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
    }

    public void removeSectionByUpStation(Station station) {
        this.sections.removeIf(section -> section.getUpStation() == station);
    }

    public void removeSectionByDownStation(Station station) {
        this.sections.removeIf(section -> section.getDownStation() == station);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public int size() {
        return this.sections.size();
    }

    public Section get(int index) {
        return this.sections.get(index);
    }

    public Section findFirstSection() {
        if (isEmpty()) return null;
        return this.sections.get(0);
    }

    public Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findFirst();
    }

    public Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst();
    }

}
