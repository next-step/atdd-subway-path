package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(){}

    public void addSection(Section section) {
        validateAddSection(section);
        sections.add(section);
    }

    private void validateAddSection(Section section){
        List<Station> stations = getStations();
        if (getStations().size() == 0) {
            return;
        }
        if (isNotValidUpStation(stations, section)) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }

        if (isDownStationExisted(stations, section)) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }
    }

    private boolean isDownStationExisted(List<Station> stations, Section section) {
        return stations.stream().anyMatch(it -> it.equals(section.getDownStation()));
    }

    private boolean isNotValidUpStation(List<Station> stations, Section section) {
        return !stations.get(stations.size() - 1).equals(section.getUpStation());
    }


    public void removeSection(Line line, Long stationId) {
        if (line.getSections().size() <= 1) {
            throw new RuntimeException();
        }

        boolean isNotValidUpStation = getStations().get(getStations().size() - 1).getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        line.getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> line.getSections().remove(it));
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


    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        sections.add(section);
    }
}
