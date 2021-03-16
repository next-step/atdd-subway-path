package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
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

    public void addSection(Section section) {
        if (getStations().isEmpty()) {
            sections.add(section);
            return;
        }

        //upstation과 매칭 됐을 때

        boolean isUpStationExist = getStations().stream().anyMatch(station -> station == section.getUpStation());
        boolean isDownStationExist = getStations().stream().anyMatch(station -> station == section.getDownStation());

        if (isDownStationExist && isUpStationExist) {
            throw new BothStationAlreadyEnrolledException("추가하고자 하는 두 역 모두 등록 되어있습니다.");
        } else if (!isUpStationExist && !isDownStationExist) {
            throw new NoneOfStationEnrolledException("추가하고자 하는 두 역 모두 등록이 안되어있습니다.");
        } else if (isUpStationExist) {
            addSectionWhenUpStationMatch(section);
        } else if (isDownStationExist) {
            addSectionWhenDownStationMatch(section);
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

    public void addSectionWhenUpStationMatch(Section section) {
        int index = getStations().indexOf(section.getUpStation());
        //뒤의 요소 변경
        if (index < sections.size()) {
            Section selectedSection = sections.get(index);
            int changedDistance = selectedSection.getDistance() - section.getDistance();
            if (changedDistance <= 0) {
                throw new InvalidDistanceException("거리가 맞지 않습니다");
            }
            selectedSection.changeUpStation(section.getDownStation());
            selectedSection.changeDistance(changedDistance);
        }
        sections.add(index, section);
    }

    public void addSectionWhenDownStationMatch(Section section) {
        int index = getStations().indexOf(section.getDownStation());
        //앞의 요소 변경
        if (index > 0) {
            Section selectedSection = sections.get(index - 1);
            int changedDistance = selectedSection.getDistance() - section.getDistance();
            if (changedDistance <= 0) throw new InvalidDistanceException("거리가 맞지 않습니다");
            selectedSection.changeDownStation(section.getUpStation());
            selectedSection.changeDistance(changedDistance);
        }
        sections.add(index, section);
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException();
        }
        int indexOfStation = getStations().indexOf(station);
        boolean isStationExistInLine = indexOfStation==-1;

        if(isStationExistInLine){
            throw new NoneOfStationEnrolledException("삭제하려는 역이 노선에 등록되어 있지 않습니다");
        }

        System.out.println("stationId = " + station.getId());
        System.out.println("last stationId" + getStations().get(getStations().size() - 1).getId());
        getStations().stream().forEach(st -> System.out.println(st.getName()));

        if(indexOfStation==0){
            sections.remove(0);
            return;
        }
        if(indexOfStation==getStations().size()-1){
            sections.remove(sections.size()-1);
            return;
        }
        int changedDistance = sections.get(indexOfStation-1).getDistance()
                + sections.get(indexOfStation).getDistance();

        sections.get(indexOfStation-1).changeDownStation(getStations().get(indexOfStation+1));
        sections.get(indexOfStation-1).changeDistance(changedDistance);
        sections.remove(indexOfStation);
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
