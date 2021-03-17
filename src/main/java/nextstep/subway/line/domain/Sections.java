package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.AlreadyExistStation;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NotExistedStation;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;

import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        validateContainsAllStations(section);
        validateSection(section);
        sections.add(section);
    }

    private void validateContainsAllStations(Section section){
        if(getStations().containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))){
            throw new AlreadyExistStation();
        }
    }

    private void validateSection(Section section) {
        if (sections.size() == 0 || isStartStationAndEndAddable(getStations(), section)) {
            return;
        }

        List<Station> stations = getStations();
        Section find = sections.stream()
                .filter(i -> isContainStation(i, section))
                .findFirst()
                .orElseThrow(() -> new NotExistedStation("등록된 역이 없습니다."));

        if (section.getDistance() >= find.getDistance()) {
            throw new InvalidDistanceException("기존에 등록된 구간보다 큽니다.");
        }

        if(isUpStationBetweenAddable(stations, section)){
            sections.get(0).changeUpStation(section);
            return;
        }

        if(isDownStationBetweenAddable(stations, section)) {
            sections.get(0).changeDownStation(section);
            return;
        }

    }

    private boolean isStartStationAndEndAddable(List<Station> stations, Section section) {
        return section.getDownStation().equals(stations.get(0)) || section.getUpStation().equals(stations.get(stations.size() - 1));
    }

    private boolean isDownStationBetweenAddable(List<Station> stations, Section section) {
        return stations.stream()
                    .anyMatch(station -> station.equals(section.getDownStation()));
    }

    private boolean isUpStationBetweenAddable(List<Station> stations, Section section) {
        return stations.stream()
                .anyMatch(station -> station.equals(section.getUpStation()));
    }

    private boolean isContainStation(Section section, Section targetSection) {
     return section.containsStation(targetSection.getUpStation()) || section.containsStation(targetSection.getDownStation());
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

    public int countTotalDistance() {
        return sections.stream()
                .mapToInt(section -> section.getDistance())
                .sum();
    }
}
