package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedDownStationException;
import nextstep.subway.exception.NotEqualLastStationException;
import nextstep.subway.exception.NotLastStationException;
import nextstep.subway.exception.SingleSectionException;

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

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (!isLastStation(section.getUpStation())) {
            throw new NotEqualLastStationException();
        }

        if (containsStation(section.getDownStation())) {
            throw new DuplicatedDownStationException();
        }
        sections.add(section);
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }

    public void remove(Station station) {
        if (!isLastStation(station)) {
            throw new NotLastStationException();
        }
        if (isSingleSection()) {
            throw new SingleSectionException();
        }
        sections.remove(getLastStationIndex());
    }

    private boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean isLastStation(Station station) {
        return getLastStation().equals(station);
    }

    private Station getLastStation() {
        return sections.get(getLastStationIndex()).getDownStation();
    }

    private boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    public boolean isSingleSection() {
        return sections.size() == 1;
    }

    private int getLastStationIndex() {
        return sections.size() - 1;
    }

    public int calcDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }
}
