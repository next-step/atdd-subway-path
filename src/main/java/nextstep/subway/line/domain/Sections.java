package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DownStationExistedException;
import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotValidUpStationException;
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
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public void removeSection(Long stationId) {
        validateRemoveSection(stationId);
        sections.removeIf(it -> it.getDownStation().getId().equals(stationId));
    }

    public Optional<Station> getNextDownStation(Station finalDownStation) {
        Optional<Section> nextSection = sections.stream()
                .filter(it -> it.getUpStation() == finalDownStation)
                .findFirst();
        return nextSection.map(Section::getDownStation);
    }

    public Station getFirstUpStation() {
        return sections.get(0).getUpStation();
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
        boolean isNotValidUpStation = isNotValidUpStation(upStation);
        if (isNotValidUpStation) {
            throw new NotValidUpStationException();
        }

        boolean isDownStationExisted = isDownStationExisted(downStation);
        if (isDownStationExisted) {
            throw new DownStationExistedException();
        }
    }

    private boolean isNotValidUpStation(Station upStation) {
        return getStations().get(getStations().size() - 1) != upStation;
    }

    private boolean isDownStationExisted(Station downStation) {
        return getStations().stream().anyMatch(it -> it == downStation);
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
}
