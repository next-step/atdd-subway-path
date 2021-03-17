package nextstep.subway.line.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Embeddable
public class Sections {

    private static final int SECTION_SIZE_LIMIT = 1;

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

    private boolean isLastSection(Section section) {
        return getLastSection().equals(section);
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
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
        checkRemoveSection(station);

        sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst()
                .ifPresent(it -> sections.remove(it));
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

    private void checkRemoveSection(Station station) {
        if (sections.size() <= SECTION_SIZE_LIMIT) {
            throw new InValidSectionSizeException();
        }

        boolean isNotValidUpStation = !getLastStation().equals(station);
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }
    }

    private Station getLastStation() {
        return getStations().get(getStations().size() - 1);
    }
}
