package subway.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<Section> sections;

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(getLastStation());

        return stations;
    }

    public void remove() {
        sections.remove(getLastIndex());
    }

    public Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public Station getLastStation() {
        return sections.get(getLastIndex()).getDownStation();
    }

    public boolean noMatchDownStation(Long sectionUpStationId) {
        return !(Objects.equals(getLastStationId(), sectionUpStationId));
    }

    private Long getLastStationId() {
        return sections.get(getLastIndex()).getDownStation().getId();
    }

    public boolean isStationExist(Long stationId) {
        Set<Long> stationIds = getAllStations().stream()
            .map(Station::getId)
            .collect(Collectors.toSet());
        return stationIds.contains(stationId);
    }

    public boolean hasSingleSection() {
        return sections.size() == 1;
    }

    public boolean isNotLastStation(Long stationId) {
        return !getLastStationId().equals(stationId);
    }


}
