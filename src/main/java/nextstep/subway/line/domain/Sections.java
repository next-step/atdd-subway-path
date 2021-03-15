package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void addSections(Section section) {
        if (isFirstSection()) {
            sections.add(section);
            return;
        }
        checkUpStation(section);
        checkDownStation(section);
        sections.add(section);
    }

    private boolean isFirstSection() {
        return getStations().size() == 0;
    }

    private void checkDownStation(Section section) {
        boolean isDownStationExisted = getStations().stream().anyMatch(it -> it == section.getDownStation());
        if (isDownStationExisted) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }
    }

    private void checkUpStation(Section section) {
        boolean isNotValidUpStation = getStations().get(getStations().size() - 1) != section.getUpStation();
        if (isNotValidUpStation) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }
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

    public void removeSection(Long stationId) {
        if (hasLastSection()) {
            throw new RuntimeException();
        }
        checkLastStation(stationId);
        removeTargetSection(stationId);
    }

    private boolean hasLastSection() {
        return sections.size() <= 1;
    }

    private void checkLastStation(Long stationId) {
        boolean isNotValidUpStation = getStations().get(getStations().size() - 1).getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }
    }

    private void removeTargetSection(Long stationId) {
        sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    public List<StationResponse> toStationResponses() {
        return getStations().stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
