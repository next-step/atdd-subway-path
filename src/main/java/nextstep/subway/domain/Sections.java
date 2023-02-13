package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nextstep.subway.common.constants.ErrorConstant.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        addValidation(section);

        if (getDownStation().equals(section.getUpStation())) {
            sections.add(section);
            return;
        }
        addMiddleStation(section);
    }

    private void addMiddleStation(Section newSection) {
        for (Section section : sections) {
            if (section.getUpStation().equals(newSection.getUpStation())) {
                sections.add(section.addStation(newSection.getDownStation(), section.getDistance() - newSection.getDistance()));
                break;
            }

            if (section.getDownStation().equals(newSection.getDownStation())) {
                sections.add(section.addStation(newSection.getUpStation(), newSection.getDistance()));
                break;
            }
        }
    }

    private void addValidation(Section newSection) {
        List<Section> containUpStation = new ArrayList<>();
        List<Section> containDownStation = new ArrayList<>();

        for (Section section : sections) {
            if (section.isContainStation(newSection.getUpStation())) {
                containUpStation.add(section);
            }

            if (section.isContainStation(newSection.getDownStation())) {
                containDownStation.add(section);
            }
        }

        if (!(containUpStation.isEmpty() || containDownStation.isEmpty())) {
            throw new IllegalArgumentException(ALREADY_ENROLL_STATION);
        }

        if (containUpStation.isEmpty() && containDownStation.isEmpty()) {
            throw new IllegalArgumentException(NOT_ENROLL_STATION);
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        stations.add(getUpStation());
        Section next = findSectionByUpStation(getUpStation());
        while (next != null) {
            stations.add(next.getDownStation());
            next = findSectionByUpStation(next.getDownStation());
        }

        return stations;
    }

    private Station getUpStation() {
        Station cur = null;
        Section next = sections.get(0);

        while (next != null) {
            cur = next.getUpStation();
            next = findSectionByDownStation(next.getUpStation());
        }

        return cur;
    }

    private Section findSectionByDownStation(Station downStation) {
        for (Section section : sections) {
            if (section.getDownStation().equals(downStation)) {
                return section;
            }
        }

        return null;
    }

    private Station getDownStation() {
        Station cur = null;
        Section next = sections.get(0);

        while (next != null) {
            cur = next.getDownStation();
            next = findSectionByUpStation(next.getDownStation());
        }

        return cur;
    }

    private Section findSectionByUpStation(Station upStation) {
        for (Section section : sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }

        return null;
    }

    public void removeSection(Station station) {
        if (!getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }
}
