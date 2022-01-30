package nextstep.subway.domain;

import nextstep.subway.domain.exception.CannotAddSectionException;

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
        if (!sections.isEmpty()) {
            addableUpStation(section);
            addableDownStation(section);
        }
        sections.add(section);
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
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, sections.get(0).getUpStation());

        return Collections.unmodifiableList(stations);
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
