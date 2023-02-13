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
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.stream().map(Section::getUpStation).findFirst().get());

        return stations;
    }

    public void removeSection(Station station) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("If there is less than one registered section, you cannot delete it.");
        }

        if (!getLastSection().isDownStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }
}
