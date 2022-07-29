package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.InvalidDistanceBetweenStationsException;
import nextstep.subway.exception.NoLastStationException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class Sections {

    @OneToMany(
            mappedBy = "line",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSection(section);
    }

    public void removeSection(Station station) {
        validateLastStation(station);
        validateSingleSection();
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(getTailStation());
        return stations;
    }

    private void addSection(Section section) {
        if (addIfNextToHead(section)
                || addIfInFrontOfHead(section)
                || addIfNextToTail(section)
                || addIfInFrontOfTail(section)) {
            return;
        }
        throw new SectionRegistrationException();
    }

    private boolean addIfInFrontOfTail(Section section) {
        if (!isInFrontOfTail(section)) {
            return false;
        }
        getLastSection().updateDownStation(section);
        sections.add(section);
        return true;
    }

    private boolean addIfNextToTail(Section section) {
        if (!isNextToTail(section)) {
            return false;
        }
        sections.add(section);
        return true;
    }

    private boolean addIfInFrontOfHead(Section section) {
        if (!isInFrontOfHead(section)) {
            return false;
        }
        sections.add(0, section);
        return true;
    }

    private boolean addIfNextToHead(Section section) {
        if (!isNextToHead(section)) {
            return false;
        }
        getFirstSection().updateUpStation(section);
        sections.add(0, section);
        return true;
    }

    private boolean containsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.containsStation(station));
    }

    private boolean isInFrontOfTail(Section section) {
        return getTailStation().equals(section.getDownStation())
                && !containsStation(section.getUpStation());
    }

    private boolean isNextToTail(Section section) {
        return getTailStation().equals(section.getUpStation())
                && !containsStation(section.getDownStation());
    }

    private boolean isInFrontOfHead(Section section) {
        return getHeadStation().equals(section.getDownStation())
                && !containsStation(section.getUpStation());
    }

    private boolean isNextToHead(Section section) {
        return getHeadStation().equals(section.getUpStation())
                && !containsStation(section.getDownStation());
    }

    private Station getTailStation() {
        return getLastSection()
                .getDownStation();
    }

    private Station getHeadStation() {
        return getFirstSection()
                .getUpStation();
    }

    private void validateLastStation(Station station) {
        Station lastStation = getTailStation();
        if (!lastStation.equals(station)) {
            throw new NoLastStationException();
        }
    }

    private void validateSingleSection() {
        if (sections.size() == 1) {
            throw new SectionRemovalException();
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

}
