package nextstep.subway.line.domain;

import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Line line, Station upStation, Station downStation, int distance) {

        final List<Station> stations = getStations();
        final Section section = new Section(line, upStation, downStation, distance);
        final boolean matchedUpStation = stations.contains(upStation);
        final boolean matchedDownStation = stations.contains(downStation);

        if (stations.size() == 0) {
            add(section);
            return;
        }

        if (matchedUpStation && matchedDownStation) {
            throw new IsExistedSectionException();
        }

        if (stations.get(stations.size() - 1) == upStation) {
            add(section);
            return;
        }

        if (stations.get(0) == downStation){
            unshift(section);
            return;
        }

        if (matchedUpStation) {
            addBetweenUpStation(section);
            return;
        }

        if (matchedDownStation) {
            addBetweenDownStation(section);
            return;
        }

        throw new DoseNotExistStationOfSectionException();
    }

    private void add(Section section) {
        sections.add(section);
    }

    private void unshift(Section section) {
        sections.add(0, section);
    }

    private void addBetweenUpStation(Section frontSection) {
        Section backSection = sections.stream()
                                      .filter(it -> it.getUpStation().equals(frontSection.getUpStation()))
                                      .findFirst()
                                      .orElseThrow(RuntimeException::new);

        int backIndex = sections.indexOf(backSection);
        int subDistance = backSection.getDistance() - frontSection.getDistance();

        if (subDistance <= 0) {
            throw new IsMoreThanDistanceException();
        }

        sections.add(backIndex, frontSection);

        backSection.update(new Section(
            backSection.getLine(),
            frontSection.getDownStation(),
            backSection.getDownStation(),
            subDistance
        ));
    }

    private void addBetweenDownStation(Section backSection) {
        Section frontSection = sections.stream()
                                  .filter(it -> it.getDownStation().equals(backSection.getDownStation()))
                                  .findFirst()
                                  .orElseThrow(RuntimeException::new);

        int frontIndex = sections.indexOf(frontSection);
        int subDistance = frontSection.getDistance() - backSection.getDistance();

        if (subDistance <= 0) {
            throw new IsMoreThanDistanceException();
        }

        sections.add(frontIndex + 1, backSection);

        frontSection.update(new Section(
            frontSection.getLine(),
            frontSection.getUpStation(),
            backSection.getUpStation(),
            subDistance
        ));
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(
                    section.getUpStation(),
                    section.getDownStation()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    public void remove(Long stationId) {
        if (sections.size() <= 1) {
            throw new HaveOnlyOneSectionException();
        }

        List<Station> stations = getStations();

        boolean isNotValidUpStation = !stations.get(stations.size() - 1).getId().equals(stationId);
        if (isNotValidUpStation) {
            throw new IsNotLastDownStationException();
        }

        sections.stream()
            .filter(it -> it.getDownStation().getId().equals(stationId))
            .findFirst()
            .ifPresent(sections::remove);
    }
}
