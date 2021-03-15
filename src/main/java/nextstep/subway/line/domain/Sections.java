package nextstep.subway.line.domain;

import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.InvalidSectionDistanceException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.SectionDuplicatedException;
import nextstep.subway.line.exception.SectionNotConnectedException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Station> nextDownStation = getNextDownStation(finalDownStation);

            if (!nextDownStation.isPresent()) {
                break;
            }

            downStation = nextDownStation.get();
            stations.add(downStation);
        }

        return stations;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }

        validateAddSection(upStation, downStation);

        if (isExistUpStationAndNotLastDownStation(upStation)) {
            addSectionsInUpStationDuplicated(line, upStation, downStation, distance);
            return;
        }

        if (isExistDownStationAndNotFirstUpStation(downStation)) {
            addSectionsInDownStationDuplicated(line, upStation, downStation, distance);
            return;
        }

        sections.add(new Section(line, upStation, downStation, distance));
    }

    public void removeSection(Long stationId) {
        validateRemoveSection(stationId);
        sections.removeIf(it -> it.isDownStationId(stationId));
    }

    public Optional<Station> getNextDownStation(Station finalDownStation) {
        Optional<Section> nextSection = sections.stream()
                .filter(it -> it.getUpStation() == finalDownStation)
                .findFirst();
        return nextSection.map(Section::getDownStation);
    }

    public Station getFirstUpStation() {
        if (sections.size() == 1) {
            return sections.get(0).getUpStation();
        }

        Station firstUpStation = sections.get(0).getUpStation();
        for (int i = 1; i < sections.size(); i++) {
            if (sections.get(i).getDownStation() == firstUpStation) {
                firstUpStation = sections.get(i).getDownStation();
            }
        }
        return firstUpStation;
    }

    public Station getLastDownStation() {
        if (sections.size() == 1) {
            return sections.get(0).getDownStation();
        }

        Station lastDownStation = sections.get(0).getDownStation();
        for (int i = 1; i < sections.size(); i++) {
            if (sections.get(i).getUpStation() == lastDownStation) {
                lastDownStation = sections.get(i).getUpStation();
            }
        }
        return lastDownStation;
    }

    public Optional<Section> getNextSection(Station finalDownStation) {
        return sections.stream()
                .filter(it -> it.getDownStation() == finalDownStation)
                .findFirst();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public int size() {
        return sections.size();
    }

    public int getTotalDistance() {
        return sections.stream()
                .map(Section::getDistance)
                .reduce(0, Integer::sum);
    }

    private Station findUpStation() {
        Station downStation = getFirstUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getNextSection(finalDownStation);
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    private void validateAddSection(Station upStation, Station downStation) {
        if (isExistSection(upStation, downStation)) {
            throw new SectionDuplicatedException();
        }

        if (isNotConnectedSection(upStation, downStation)) {
            throw new SectionNotConnectedException();
        }
    }

    private boolean isExistSection(Station upStation, Station downStation) {
        return sections.stream()
                .anyMatch(it -> it.getUpStation() == upStation && it.getDownStation() == downStation);
    }

    private boolean isNotConnectedSection(Station upStation, Station downStation) {
        return !getStations().contains(upStation) && !getStations().contains(downStation);
    }

    private void validateRemoveSection(Long stationId) {
        if (isHasNoneOrOneSection()) {
            throw new HasNoneOrOneSectionException();
        }

        if (isNotValidUpStation(stationId)) {
            throw new NotLastStationException();
        }
    }

    private boolean isHasNoneOrOneSection() {
        return sections.size() <= 1;
    }

    private boolean isNotValidUpStation(Long stationId) {
        return !getStations().get(getStations().size() - 1).getId().equals(stationId);
    }

    private boolean isExistUpStationAndNotLastDownStation(Station upStation) {
        return getStations().contains(upStation) && getLastDownStation() != upStation;
    }

    private void addSectionsInUpStationDuplicated(Line line, Station upStation, Station downStation, int distance) {
        Section section = getSections().stream()
                .filter(it -> it.getUpStation() == upStation)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        if (section.getDistance() <= distance) {
            throw new InvalidSectionDistanceException();
        }

        Station prevDownStation = section.getDownStation();
        int prevDistance = section.getDistance();

        sections.removeIf(it -> it.isDownStationId(prevDownStation.getId()));

        sections.add(new Section(line, upStation, downStation, distance));
        sections.add(new Section(line, downStation, prevDownStation,prevDistance - distance));
    }

    private boolean isExistDownStationAndNotFirstUpStation(Station downStation) {
        return getFirstUpStation() != downStation && getStations().contains(downStation);
    }

    private void addSectionsInDownStationDuplicated(Line line, Station upStation, Station downStation, int distance) {
        Section section = getSections().stream()
                .filter(it -> it.getDownStation() == downStation)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        if (section.getDistance() <= distance) {
            throw new InvalidSectionDistanceException();
        }

        Station prevUpStation = section.getUpStation();
        int prevDistance = section.getDistance();

        sections.removeIf(it -> it.isUpStationId(prevUpStation.getId()));

        sections.add(new Section(line, prevUpStation, upStation,prevDistance - distance));
        sections.add(new Section(line, upStation, downStation, distance));
    }
}
