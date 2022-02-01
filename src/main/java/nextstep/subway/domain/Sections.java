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

        addFirstUpSection(section);

        sections.add(section);
    }

    private void addFirstUpSection(Section section) {
        boolean addableFirstStation = findFirstUpStation().equals(section.getDownStation());
        boolean hasNoUpStation = sections.stream()
                .anyMatch(section1 -> section1.containsStation(section.getUpStation()));

        if (addableFirstStation && hasNoUpStation) {
            sections.add(section);
        }
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

    private void addableUpStation(Section section) {
        Section lastSection = getLastSection();

        if (!lastSection.isAddableLastSection(section)) {
            String lastStationName = lastSection.downStationName();
            String addUpStationName = section.upStationName();
            throw new CannotAddSectionException(lastStationName, addUpStationName);
        }
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private void addableDownStation(Section addSection) {
        Station downStation = addSection.getDownStation();
        boolean containsStation = sections.stream()
                .anyMatch(section -> section.containsStation(downStation));
        if (containsStation) {
            throw new CannotAddSectionException(addSection.downStationName());
        }
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
