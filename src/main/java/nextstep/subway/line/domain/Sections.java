package nextstep.subway.line.domain;

import nextstep.subway.exception.CanNotFoundSectionToAddException;
import nextstep.subway.exception.InValidSectionSizeException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (isEmptyStations()) {
            sections.add(section);
            return;
        }

        if(getLastStation().equals(section.getUpStation())
                || getFirstStation().equals(section.getDownStation())
        ) {
            sections.add(section);
            return;
        }

        Section matchSection = sections.stream()
                .filter(it -> it.hasMatchStation(section))
                .findFirst()
                .orElseThrow(() -> new CanNotFoundSectionToAddException());

        final int distance = matchSection.calculateDistance(section.getDistance());
        matchSection.update(section, distance);
        sections.add(section);
    }

    private Station getFirstStation() {
        return getStations().get(0);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new InValidSectionSizeException();
        }

        Optional<Section> down = findDownSection(station);
        Optional<Section> up = findUpSection(station);

        if (up.isPresent() && down.isPresent()) {
            Section downSection = down.get();
            Section upSection = up.get();

            int distance = downSection.addDistance(upSection.getDistance());
            sections.add(Section.of(
                    upSection.getLine(),
                    downSection.getUpStation(),
                    upSection.getDownStation(),
                    distance));
        }
        up.ifPresent(this::removeSection);
        down.ifPresent(this::removeSection);
    }

    private Optional<Section> findUpSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst();
    }

    private Optional<Section> findDownSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst();
    }

    private boolean matchAny(Section section, Station station) {
        return section.getDownStation().equals(station) || section.getUpStation().equals(station);
    }

    private boolean isEmptyStations() {
        return getStations().size() == 0;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    private Station getLastStation() {
        return getStations().get(getStations().size() - 1);
    }

    private void removeSection(Section section) {
        sections.remove(section);
    }
}
