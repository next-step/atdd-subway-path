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

    public void add(Section section) {

    }

    public int size() {
        return sections.size();
    }

    public List<Station> getStations(Station upStation) {
        return null;
    }

    public void remove(Section section) {
        ;
    }

    public Section findSectionByDownStation(Station downStation) {
        return null;
    }

    public boolean hasStation(Station station) {
        return false;
    }
}
