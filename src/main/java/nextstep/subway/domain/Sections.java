package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();

        sections.stream()
            .filter(it -> it.getUpStation().equals(upStation))
            .findFirst()
            .ifPresent(it -> it.updateUpStation(downStation, distance));

        sections.stream()
            .filter(it -> it.getDownStation().equals(downStation))
            .findFirst()
            .ifPresent(it -> it.updateDownStation(upStation, distance));

        sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStationsInOrder() {
        Station firstStation = findFirstLineStation();

        List<Station> stations = new ArrayList<>();
        stations.add(firstStation);

        Optional<Section> currentSection = sections.stream()
            .filter(it -> it.hasUpStation(firstStation))
            .findFirst();

        while (currentSection.isPresent()) {
            Station downStation = currentSection.get().getDownStation();
            stations.add(downStation);
            currentSection = sections.stream()
                .filter(it -> it.hasUpStation(downStation))
                .findFirst();
        }

        return stations;
    }

    private Station findFirstLineStation() {
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return sections.stream()
            .map(Section::getUpStation)
            .filter(it -> !downStations.contains(it))
            .findFirst()
            .orElseThrow();
    }

    public void remove(Station station) {
        if (isSingleSection() || !isLastLineStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    private boolean isSingleSection() {
        return sections.size() == 1;
    }

    private boolean isLastLineStation(Station station) {
        List<Station> stations = getStationsInOrder();
        return stations.get(stations.size() - 1).equals(station);
    }

    public List<Section> getSections() {
        return sections;
    }
}
