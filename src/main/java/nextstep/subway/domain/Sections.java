package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Optional<Section> findSectionWithUpStation(Station downStation) {
        return sections
                .stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findAny();
    }

    public Section findLastUpSection() {
        List<Station> downStations = findAllDownStations();

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }

    public Section findLastDownSection() {
        List<Station> upStations = findAllUpStations();

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findAny().orElseThrow(RuntimeException::new);
    }


    private List<Station> findAllUpStations() {
        return sections
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findAllDownStations() {
        return sections
                .stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int getSize() {
        return sections.size();
    }

    public void remove(Station station) {
        Section toRemoveSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny()
                .orElseThrow(RuntimeException::new);

        sections.remove(toRemoveSection);
    }

    public boolean isLastDownStation(Station station) {
        return findLastDownSection().getDownStation().equals(station);
    }
}
