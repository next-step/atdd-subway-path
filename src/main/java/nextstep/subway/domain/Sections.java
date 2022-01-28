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

        for (Section section : sections) {
            if (section.getUpStation().equals(newSection.getUpStation())) {
                if (newSection.hasSameOrLongerDistanceThan(section)) {
                    throw new DataIntegrityViolationException("유효하지 않은 구간 길이.");
                }
                section.setUpStation(newSection.getDownStation());
                sections.add(newSection);
                return;
            }

            if (section.getDownStation().equals(newSection.getDownStation())) {
                if (newSection.hasSameOrLongerDistanceThan(section)) {
                    throw new DataIntegrityViolationException("유효하지 않은 구간 길이.");
                }
                section.setDownStation(newSection.getUpStation());
                sections.add(newSection);
                return;
            }
        }

        sections.add(newSection);
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

        Station upStation = sections.get(0).getUpStation();
        Station downStation = sections.get(0).getDownStation();

        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);

        boolean frontSideIsDone = false;
        boolean rearSideIsDone = false;
        while (!frontSideIsDone) {
            boolean found = false;
            for (Section section : sections) {
                if (upStation.equals(section.getDownStation())) {
                    upStation = section.getUpStation();
                    stations.add(0, upStation);
                    found = true;
                }
            }
            if (!found) {
                frontSideIsDone = true;
            }
        }

        while (!rearSideIsDone) {
            boolean found = false;
            for (Section section : sections) {
                if (downStation.equals(section.getUpStation())) {
                    downStation = section.getDownStation();
                    stations.add(downStation);
                    found = true;
                }
            }
            if (!found) {
                rearSideIsDone = true;
            }
        }

        return stations;
    }
}
