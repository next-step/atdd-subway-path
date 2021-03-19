package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.AlreadyExistStation;
import nextstep.subway.line.domain.exception.CannotRemoveStation;
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
        swapStation(section);
        sections.add(section);
    }


    private void validateContainsAllStations(Section section){
        if(getStations().containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))){
            throw new AlreadyExistStation();
        }
    }

    private void swapStation(Section section) {
        if (sections.size() == 0 || isStartStationAndEndAddable(getStations(), section)) {
            return;
        }
        validateSwapAvailable(section);

        List<Station> stations = getStations();
        if(isUpStationBetweenAddable(stations, section)){
            sections.get(0).changeUpStation(section);
            return;
        }

        if(isDownStationBetweenAddable(stations, section)) {
            sections.get(0).changeDownStation(section);
        }
    }

    private void validateSwapAvailable(Section section) {
        Section find = sections.stream()
                .filter(i -> isContainStation(i, section))
                .findFirst()
                .orElseThrow(() -> new NotExistedStation("등록된 역이 없습니다."));

        if (section.getDistance() >= find.getDistance()) {
            throw new InvalidDistanceException("기존에 등록된 구간보다 큽니다.");
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

    public void removeSection(Long stationId) {
        validateRemoveSection();

        boolean isCenter = isCenterStation(stationId);
        if(isCenter) {
            Section lastSection = sections.get(sections.size() -1);
            sections.get(0).union(lastSection);
        }

        Section section = sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId || it.getUpStation().getId() == stationId)
                .findFirst()
                .orElseThrow(() -> new CannotRemoveStation("노선에 해당되는 역이 없습니다."));
        sections.remove(section);
    }

    private boolean isCenterStation(Long stationId) {
       return  sections.stream().anyMatch(it -> it.getDownStation().getId() == stationId) &&
               sections.stream().anyMatch(it -> it.getUpStation().getId() == stationId);
    }

    private void validateRemoveSection() {
        if (sections.size() <= 1) {
            throw new CannotRemoveStation("노선에 등록된 구간이 1개 이하일때는 삭제할 수 없습니다.");
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
