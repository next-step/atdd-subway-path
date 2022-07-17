package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        stations.add(getFirstSection().getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public void deleteLastSection(Station station) {
        if (isInValidSize()) {
            throw new IllegalStateException();
        }
        Section lastSection = sections.get(getLastIndex());
        if (!lastSection.hasDownStation(station)) {
            throw new IllegalStateException();
        }
        sections.remove(lastSection);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int getSize() {
        return sections.size();
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    private boolean isInValidSize() {
        return sections.isEmpty();
    }
}
