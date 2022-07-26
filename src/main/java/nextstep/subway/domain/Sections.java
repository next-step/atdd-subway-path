package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return this.sections;
    }

    public Section getLastSection() {
        if (this.sections.isEmpty()) {
            return null;
        }

        return this.sections.get(this.sections.size() - 1);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public boolean isEmpty(){
        return this.sections.isEmpty();
    }

    public List<Station> getStations(){
        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, this.sections.get(0).getUpStation());
        return stations;
    }

    public void deleteStation(Station station) {
        if (!this.getLastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(getLastSection());
    }
}
