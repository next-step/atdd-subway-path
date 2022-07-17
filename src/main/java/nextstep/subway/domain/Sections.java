package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    private static final int FIRST_SECTION_INDEX = 0;

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
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Station> stations = new ArrayList<>();
        stations.add(getFirstUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void deleteLastSection(Station station) {
        if (isInValidSize()) {
            throw new IllegalStateException();
        }
        Section lastSection = getLastSection();
        if (lastSection.dontHasDownStation(station)) {
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
        return sections.get(FIRST_SECTION_INDEX);
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    private boolean isInValidSize() {
        return sections.isEmpty();
    }

    private Station getFirstUpStation() {
        return getFirstSection().getUpStation();
    }

    private Section getLastSection() {
        return sections.get(getLastIndex());
    }
}
