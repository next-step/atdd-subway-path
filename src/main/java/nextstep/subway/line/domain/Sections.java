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

        if(isDownStationExist && isUpStationExist){
            throw new BothStationAlreadyEnrolledException("추가하고자 하는 두 역 모두 등록 되어있습니다.");
        }else if(!isUpStationExist && !isDownStationExist){
            throw new NoneOfStationEnrolledException("추가하고자 하는 두 역 모두 등록이 안되어있습니다.");
        }else if(isUpStationExist){
            addSectionWhenUpStationMatch(section);
        }else if(isDownStationExist){
            addSectionWhenDownStationMatch(section);
        }
    }

    public List<Station> getStations(){
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

    public void addSectionWhenUpStationMatch(Section section){
        int index = getStations().indexOf(section.getUpStation());
        //뒤의 요소 변경
        if(index<sections.size()){
            Section selectedSection = sections.get(index);
            int changedDistance = selectedSection.getDistance()-section.getDistance();
            if(changedDistance<=0) throw new InvalidDistanceException("거리가 맞지 않습니다");
            selectedSection.changeUpStation(section.getDownStation());
            selectedSection.changeDistance(changedDistance);
        }
        sections.add(index, section);
        sections.stream().forEach((sec) -> System.out.println("=======" + sec.getUpStation().getName() + ", " + sec.getDownStation().getName()));
        getStations().stream().forEach((station -> System.out.println("***********" + station.getName())));
    }

    public void addSectionWhenDownStationMatch(Section section){
        int index = getStations().indexOf(section.getDownStation());
        //앞의 요소 변경
        if(index>0){
            Section selectedSection = sections.get(index-1);
            int changedDistance = selectedSection.getDistance()-section.getDistance();
            if(changedDistance<=0) throw new InvalidDistanceException("거리가 맞지 않습니다");
            selectedSection.changeDownStation(section.getUpStation());
            selectedSection.changeDistance(changedDistance);
        }
        sections.add(index, section);
    }

    public void removeSection(Long stationId) {
        if (sections.size() <= 1) {
            throw new RuntimeException();
        }

        boolean isNotValidUpStation = getStations().get(getStations().size() - 1).getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }

    public List<Section> getSectionList(){
        return sections;
    }
}
