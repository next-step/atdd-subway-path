package nextstep.subway.domain;

import static nextstep.subway.exception.CommonExceptionMessages.ALREADY_HAS_STATIONS;
import static nextstep.subway.exception.CommonExceptionMessages.NOT_HAS_ANY_STATIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.springframework.dao.DataIntegrityViolationException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

         sectionAdd(newSection);
    }

    private void sectionAdd(Section newSection) {
        validateAddable(newSection);

        // findConnectStation(newSection

        sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .forEach(section -> {
                section.hasEnoughDistanceForAddingOrElseThrow(newSection);
                section.setUpStation(newSection.getDownStation());
            });

        sections.stream()
            .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
            .forEach(section -> {
                section.hasEnoughDistanceForAddingOrElseThrow(newSection);
                section.setDownStation(newSection.getUpStation());
            });

        sections.add(newSection);
    }

    private void validateAddable(Section newSection) {
        if (hasAllStationsOf(newSection)) {
            throw new DataIntegrityViolationException(ALREADY_HAS_STATIONS);
        }

        if (!existAnyStationsOf(newSection)) {
            throw new DataIntegrityViolationException(NOT_HAS_ANY_STATIONS);
        }
    }

//    public void add(Section newSection) {
//        if (!sections.isEmpty()) {
//            validateAddable(newSection);
//        }
//
//        sections.stream()
//            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
//            .forEach(section -> {
//                section.hasEnoughDistanceForAddingOrElseThrow(newSection);
//                section.setUpStation(newSection.getDownStation());
//            });
//
//        sections.stream()
//            .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
//            .forEach(section -> {
//                section.hasEnoughDistanceForAddingOrElseThrow(newSection);
//                section.setDownStation(newSection.getUpStation());
//            });
//
//        sections.add(newSection);
//    }

    public boolean hasAllStationsOf(Section section) {
        return containsStation(section.getUpStation())
            && containsStation(section.getDownStation());
    }

    public boolean existAnyStationsOf(Section section) {
        return containsStation(section.getUpStation())
            || containsStation(section.getDownStation());
    }

    private boolean containsStation(Station station) {
        return sections.stream()
            .anyMatch(section -> section.hasStation(station));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int size() {
        return sections.size();
    }

    public Section get(int index) {
        return sections.get(index);
    }

    public void remove(int index) {
        sections.remove(index);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        makeFrontSideOf(stations);
        makeRearSideOf(stations);

        return stations;
    }

    private void makeFrontSideOf(List<Station> stations) {
        Station upStation = sections.get(0).getUpStation();
        stations.add(upStation);

        boolean found;
        while (true) {
            found = false;
            for (Section section : sections) {
                if (canBeConnectedFront(upStation, section)) {
                    upStation = section.getUpStation();
                    stations.add(0, upStation);
                    found = true;
                }
            }

            if (!found) {
                return;
            }
        }
    }

    private void makeRearSideOf(List<Station> stations) {
        Station downStation = sections.get(0).getDownStation();
        stations.add(downStation);

        boolean found;
        while (true) {
            found = false;
            for (Section section : sections) {
                if (canBeConnectedRear(downStation, section)) {
                    downStation = section.getDownStation();
                    stations.add(downStation);
                    found = true;
                }
            }

            if (!found) {
                return;
            }
        }
    }

    private boolean canBeConnectedFront(Station upStation, Section section) {
        return upStation.equals(section.getDownStation());
    }

    private boolean canBeConnectedRear(Station downStation, Section section) {
        return downStation.equals(section.getUpStation());
    }
}
