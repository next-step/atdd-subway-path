package nextstep.subway.line.domain;

import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (stations.get(stations.size() - 1) == upStation || stations.get(0) == downStation) {
            add(section);
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

    private void addBetweenUpStation(Section frontSection) {
        Section backSection = sections.stream()
                                      .filter(it -> it.getUpStation().equals(frontSection.getUpStation()))
                                      .findFirst()
                                      .orElseThrow(RuntimeException::new);

        int distance = backSection.getDistance() - frontSection.getDistance();
        validateDistance(distance);

        sections.add(frontSection);

        backSection.update(new Section(
            backSection.getLine(),
            frontSection.getDownStation(),
            backSection.getDownStation(),
            distance
        ));
    }

    private void addBetweenDownStation(Section backSection) {
        Section frontSection = sections.stream()
                                  .filter(it -> it.getDownStation().equals(backSection.getDownStation()))
                                  .findFirst()
                                  .orElseThrow(RuntimeException::new);

        int distance = frontSection.getDistance() - backSection.getDistance();
        validateDistance(distance);

        sections.add(backSection);

        frontSection.update(new Section(
            frontSection.getLine(),
            frontSection.getUpStation(),
            backSection.getUpStation(),
            distance
        ));
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IsMoreThanDistanceException();
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        Station nextStation = downStation;

        while (nextStation != null) {
            Station finalDownStation = downStation = nextStation;
            stations.add(downStation);

            nextStation = sections.stream()
                .filter(it -> it.getUpStation() == finalDownStation)
                .findFirst()
                .map(Section::getDownStation)
                .orElse(null);
        }

        return stations;
    }

    private Station findUpStation() {
        Station upStation = sections.get(0).getUpStation();
        Station nextStation = upStation;
        while (nextStation != null) {
            Station finalDownStation = upStation = nextStation;
            nextStation = sections.stream()
                .filter(it -> it.getDownStation() == finalDownStation)
                .findFirst()
                .map(Section::getUpStation)
                .orElse(null);
        }
        return upStation;
    }

    public void remove(Long stationId) {
        if (sections.size() <= 1) {
            throw new HaveOnlyOneSectionException();
        }

        List<Section> willRemoveSections = sections.stream()
                                                   .filter(it -> (
                                                       it.getUpStation().getId().equals(stationId) ||
                                                       it.getDownStation().getId().equals(stationId)
                                                   ))
                                                   .collect(Collectors.toList());

        if (willRemoveSections.size() == 0) {
            throw new DoseNotExistStationOfSectionException("노선에 없는 역은 삭제할 수 없습니다.");
        }

        willRemoveSections.forEach(sections::remove);

        if (willRemoveSections.size() == 1) {
            return;
        }

        List<Section> sortedSections = willRemoveSections.stream()
            .sorted((front, back) -> (
                front.getDownStation().getId().equals(stationId) &&
                back.getUpStation().getId().equals(stationId)
            ) ? 1 : 0)
            .collect(Collectors.toList());

        sections.add(
            new Section(
                willRemoveSections.get(0).getLine(),
                willRemoveSections.get(0).getUpStation(),
                willRemoveSections.get(1).getDownStation(),
                sortedSections.stream()
                    .map(Section::getDistance)
                    .reduce(0, Math::addExact)
            )
        );
    }
}
