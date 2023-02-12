package nextstep.subway.domain;

import nextstep.subway.exception.BothSectionStationsNotExistsInLineException;
import nextstep.subway.exception.CannotDeleteSoleSectionException;
import nextstep.subway.exception.SectionStationsAlreadyExistsInLineException;
import nextstep.subway.exception.SectionWithStationNotExistsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validateSection(section);

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();

        sections.stream()
            .filter(it -> it.hasUpStation(upStation))
            .findFirst()
            .ifPresent(it -> {
                it.updateUpStation(downStation);
                it.decreaseDistance(distance);
            });

        sections.stream()
            .filter(it -> it.hasDownStation(downStation))
            .findFirst()
            .ifPresent(it -> {
                it.updateDownStation(upStation);
                it.decreaseDistance(distance);
            });

        sections.add(section);
    }

    private void validateSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        List<Station> stations = getStationsInOrder();
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new SectionStationsAlreadyExistsInLineException(upStation.getName(), downStation.getName());
        }

        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new BothSectionStationsNotExistsInLineException(upStation.getName(), downStation.getName());
        }
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStationsInOrder() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

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
        if (isSingleSection()) {
            throw new CannotDeleteSoleSectionException();
        }
        removeSection(station);
    }

    private boolean isSingleSection() {
        return sections.size() == 1;
    }

    private void removeSection(Station station) {
        if (isFirstLineStation(station)) {
            removeFirstSection(station);
            return;
        }

        if (isLastLineStation(station)) {
            removeLastSection();
            return;
        }

        removeIntermediateSection(station);
    }

    private boolean isFirstLineStation(Station station) {
        List<Station> stations = getStationsInOrder();
        return stations.get(0).equals(station);
    }

    private void removeLastSection() {
        sections.remove(sections.size() - 1);
    }

    private void removeFirstSection(Station station) {
        Section section = sections.stream()
            .filter(it -> it.hasUpStation(station))
            .findFirst()
            .orElseThrow(() -> new SectionWithStationNotExistsException(station.getName()));
        sections.remove(section);
    }

    private boolean isLastLineStation(Station station) {
        List<Station> stations = getStationsInOrder();
        return stations.get(stations.size() - 1).equals(station);
    }

    private void removeIntermediateSection(Station station) {
        Section upSection = sections.stream()
            .filter(it -> it.hasDownStation(station))
            .findFirst()
            .orElseThrow(() -> new SectionWithStationNotExistsException(station.getName()));

        Section downSection = sections.stream()
            .filter(it -> it.hasUpStation(station))
            .findFirst()
            .orElseThrow(() -> new SectionWithStationNotExistsException(station.getName()));

        upSection.updateDownStation(downSection.getDownStation());
        upSection.increaseDistance(downSection.getDistance());

        sections.remove(downSection);
    }

    public List<Section> getSections() {
        return sections;
    }
}
