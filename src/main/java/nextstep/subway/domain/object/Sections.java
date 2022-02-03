package nextstep.subway.domain.object;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.security.InvalidParameterException;
import java.util.ArrayList;
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

    public Section last() {
        return this.values.get(this.values.size() - 1);
    }

    public Station lastDownStation() {
        return last().getDownStation();
    }

    public Long lastDownStationId() {
        return last().getDownStationId();
    }

    public List<Station> getAllStations() {
        List<Station> stations = this.values.stream()
                .sorted(Comparator.comparing(Section::getId))
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(lastDownStation());
        return stations;
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
        this.values.remove(last());
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
