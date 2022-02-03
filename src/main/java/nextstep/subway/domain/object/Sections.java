package nextstep.subway.domain.object;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values;

    public Sections() {
        this.values = new ArrayList<>();
    }

    public void add(Section section) {
        if (!validateAddSection(section)) {
            throw new InvalidParameterException();
        }
        this.values.add(section);
    }

    public Section lastSection() {
        Station lastStation = getLastStation();
        return this.values.stream()
                .filter(value ->
                        value.getDownStation().equals(lastStation)
                ).findFirst()
                .orElse(null);
    }

    private Station getLastStation() {
        List<Station> stations = getAllStations();
        return stations.get(stations.size() - 1);
    }

    public Station lastDownStation() {
        return lastSection().getDownStation();
    }

    public Long lastDownStationId() {
        return lastSection().getDownStationId();
    }

    public List<Station> getAllStations() {
        Section firstSection = getFirstSection();

        if (firstSection == null) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        stations.add(firstSection.getUpStation());

        return getOrderedStations(stations, firstSection.getDownStation());
    }

    private Section getFirstSection() {
        Station station = getFirstStation();

        if (station == null) {
            return null;
        }

        return this.values.stream()
                .filter(value ->
                        value.getUpStation().equals(station)
                ).findFirst()
                .orElse(null);

    }

    private Station getFirstStation() {
        List<Station> upStations = values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = values.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return upStations.stream()
                .filter(upStation ->
                        !downStations.contains(upStation)
                ).findFirst()
                .orElse(null);
    }

    private List<Station> getOrderedStations(List<Station> stations, Station downStation) {
        if (downStation == null) {
            return stations;
        }
        stations.add(downStation);

        Station nextDownStation = this.values.stream()
                .filter(value ->
                        value.getUpStation().equals(downStation)
                ).map(Section::getDownStation)
                .findFirst()
                .orElse(null);

        return getOrderedStations(stations, nextDownStation);
    }

    public int size() {
        return this.values.size();
    }

    public boolean validateAddSection(Section section) {
        if (values.isEmpty()) {
            return true;
        }

        if (!equalsLastDownStation(section.getUpStation())) {
            return false;
        }

        return !checkDuplicatedDownStation(section.getDownStation());
    }

    public boolean equalsLastDownStation(Station upStation) {
        return this.lastDownStation().equals(upStation);
    }

    public boolean checkDuplicatedDownStation(Station downStation) {
        return this.values.stream()
                .anyMatch(section ->
                        section.getUpStation().equals(downStation) ||
                                section.getDownStation().equals(downStation)
                );
    }

    public void removeLastSection(Long stationId) {
        if (!validateRemoveSection(stationId)) {
            throw new InvalidParameterException();
        }
        this.values.remove(lastSection());
    }

    public boolean validateRemoveSection(Long stationId) {
        if (isSmallerMinimumSize()) {
            return false;
        }

        return lastDownStationId().equals(stationId);
    }


    private boolean isSmallerMinimumSize() {
        return this.values.size() <= 1;
    }
}
