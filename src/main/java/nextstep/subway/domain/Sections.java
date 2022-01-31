package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int LAST_INDEX_VALUE = 1;
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        sectionList.add(section);
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public List<Station> getStationList() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sectionList.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(FIRST_INDEX, firstStation());
        return Collections.unmodifiableList(stations);
    }

    public void deleteSection(Station station) {
        if (!lastStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sectionList.remove(lastIndex());
    }

    private Station firstStation() {
        return sectionList.get(FIRST_INDEX).getUpStation();
    }

    private Station lastStation() {
        return sectionList.get(lastIndex()).getDownStation();
    }

    private int lastIndex() {
        return sectionList.size() - LAST_INDEX_VALUE;
    }
}
