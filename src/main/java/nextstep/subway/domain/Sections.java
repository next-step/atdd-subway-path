package nextstep.subway.domain;

import nextstep.subway.domain.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {

        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean hasUpStation = hasUpStation(section.getUpStation());
        boolean hasDownStation = hasDownStation(section.getDownStation());

        if (hasUpStation && hasDownStation) {
            throw new CannotAddSectionException();
        }

        addFirstUpSection(section);
        addLastDownSection(section);
    }

    private void addFirstUpSection(Section section) {
        boolean addableFirstStation = findFirstUpStation().equals(section.getDownStation());
        boolean hasStation = hasStation(section.getUpStation());

        if (addableFirstStation && !hasStation) {
            sections.add(section);
        }
    }

    private void addLastDownSection(Section section) {
        boolean addableLastStation = findLastDownStation().equals(section.getUpStation());
        boolean hasStation = hasStation(section.getDownStation());

        if (addableLastStation && !hasStation) {
            sections.add(section);
        }
    }

    private boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section1 -> section1.containsStation(station));
    }


    private boolean hasUpStation(Station upStation) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStation(upStation));
    }

    private boolean hasDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.hasSameDownStation(downStation));
    }

    private Station findFirstUpStation() {
        Station station = sections.get(0).getUpStation();
        return findFirstUpStation(station);
    }

    private Station findFirstUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(upStation))
                .findFirst()
                .map(Section::getUpStation)
                .map(this::findFirstUpStation)
                .orElse(upStation);
    }

    private Station findLastDownStation() {
        Station station = sections.get(0).getDownStation();
        return findLastDownStation(station);
    }

    private Station findLastDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst()
                .map(Section::getDownStation)
                .map(this::findLastDownStation)
                .orElse(downStation);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        Station findUpStation = findFirstUpStation();
        stations.add(findUpStation);

        addInOrderOfStations(stations, findUpStation);

        return Collections.unmodifiableList(stations);
    }

    private void addInOrderOfStations(List<Station> stations, Station findUpStation) {
        for (int i = 0; i < sections.size(); i++) {
            Station upStation = findUpStation;
            Station station = sections.stream()
                    .filter(section -> section.hasSameUpStation(upStation))
                    .findFirst()
                    .map(Section::getDownStation)
                    .orElseThrow(IllegalArgumentException::new);
            stations.add(station);
            findUpStation = station;
        }
    }

    public List<Section> get() {
        return Collections.unmodifiableList(sections);
    }

    public void deleteSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }
}
