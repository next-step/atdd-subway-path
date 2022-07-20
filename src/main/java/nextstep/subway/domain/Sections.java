package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void add(Line line, Station upStation, Station downStation, int distance) {
        var section = new Section(line, upStation, downStation, distance);
        sectionList.add(section);
    }

    public void removeByStation(Station downStation) {
        if (isNotLastStation(downStation)) {
            throw new IllegalArgumentException();
        }
        sectionList.remove(sectionList.size() - 1);
    }

    public List<Station> getStations() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        var stations = sectionList.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, sectionList.get(0).getUpStation());

        return stations;
    }

    private boolean isNotLastStation(Station downStation) {
        return !sectionList.get(sectionList.size() - 1).getDownStation().equals(downStation);
    }

}
