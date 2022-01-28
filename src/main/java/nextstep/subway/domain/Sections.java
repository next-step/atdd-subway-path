package nextstep.subway.domain;

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
        if (!sections.isEmpty()) {
            validateAddable(newSection);
        }

        sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .forEach(section -> {
                validateDistance(newSection, section);
                section.setUpStation(newSection.getDownStation());
            });

        sections.stream()
            .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
            .forEach(section -> {
                validateDistance(newSection, section);
                section.setDownStation(newSection.getUpStation());
            });

        sections.add(newSection);
    }

    private void validateDistance(Section newSection, Section section) {
        if (newSection.hasSameOrLongerDistanceThan(section)) {
            throw new DataIntegrityViolationException("유효하지 않은 구간 길이.");
        }
    }


    private void validateAddable(Section newSection) {
        if (alreadyHasStationsOf(newSection)) {
            throw new DataIntegrityViolationException("추가하려는 구간의 두 역 모두 이미 있음.");
        }

        if (notExistAnyStationsOf(newSection)) {
            throw new DataIntegrityViolationException("추가하려는 구간의 두 역 중 하나도 없음.");
        }
    }

    public boolean alreadyHasStationsOf(Section section) {
        return containsStation(section.getUpStation())
            && containsStation(section.getDownStation());
    }

    private boolean notExistAnyStationsOf(Section section) {
        if (!containsStation(section.getUpStation())
            && !containsStation(section.getDownStation())) {
            return true;
        }
        return false;
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
        Boolean frontSideIsDone = false;
        while (!frontSideIsDone) {
            found = false;
            for (Section section : sections) {
                if (canBeConnectedFront(upStation, section)) {
                    upStation = section.getUpStation();
                    stations.add(0, upStation);
                    found = true;
                }
            }
            if (!found) {
                frontSideIsDone = true;
            }
        }
    }

    private void makeRearSideOf(List<Station> stations) {
        Station downStation = sections.get(0).getDownStation();
        stations.add(downStation);

        boolean found;
        boolean rearSideIsDone = false;
        while (!rearSideIsDone) {
            found = false;
            for (Section section : sections) {
                if (canBeConnectedRear(downStation, section)) {
                    downStation = section.getDownStation();
                    stations.add(downStation);
                    found = true;
                }
            }
            if (!found) {
                rearSideIsDone = true;
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
