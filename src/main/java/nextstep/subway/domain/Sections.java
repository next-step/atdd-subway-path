package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public int size() {
        return sections.size();
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void add(Section... section) {
        sections.addAll(List.of(section));
    }

    public Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    public List<StationResponse> createStationResponses() {
        return getStations().stream()
                .map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList());
    }

    public void remove(Section... section) {
        sections.removeAll(List.of(section));
    }

    public void removeLastSection(Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.isDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }
}
