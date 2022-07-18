package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public Section get(int index) {
        return this.sections.get(index);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    public int size() {
        return this.sections.size();
    }


    public void deleteStation(Station station) {
        if (!station.equals(lastStation())) {
            throw new IllegalArgumentException();
        }

        deleteLastStation();
    }

    private Station lastStation() {
        return this.sections.get(sectionsLastIndex()).getDownStation();
    }

    private void deleteLastStation() {
        this.sections.remove(sectionsLastIndex());
    }

    private int sectionsLastIndex() {
        return this.sections.size() - 1;
    }
}
