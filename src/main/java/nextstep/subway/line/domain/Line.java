package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addSection(upStation, downStation, distance);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance){
        sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations(){
        if (getSections().isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = this.getSections().stream()
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
        Station downStation = getSections().get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getSections().stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getStations().size() == 0) {
            getSections().add(new Section(this, upStation, downStation, distance));
            return;
        }

        //upstation과 매칭 됐을 때

        boolean isUpStationExist = getStations().stream().anyMatch(station -> station == upStation);
        boolean isDownStationExist = getStations().stream().anyMatch(station -> station == downStation);

        if(isDownStationExist && isUpStationExist){
            throw new BothStationAlreadyEnrolledException("추가하고자 하는 두 역 모두 등록 되어있습니다.");
        }else if(!isUpStationExist && !isDownStationExist){
            throw new NoneOfStationEnrolledException("추가하고자 하는 두 역 모두 등록이 안되어있습니다.");
        }else if(isUpStationExist){
            addSectionWhenUpStationMatch(upStation, downStation, distance);
        }else if(isDownStationExist){
            addSectionWhenDownStationMatch(upStation, downStation, distance);
        }
    }

    public void addSectionWhenUpStationMatch(Station upStation, Station downStation, int distance){
        int index = getStations().indexOf(upStation);
        //뒤의 요소 변경
        if(index<getSections().size()){
            Section section = getSections().get(index);
            int changedDistance = section.getDistance()-distance;
            if(changedDistance<=0) throw new InvalidDistanceException("거리가 맞지 않습니다");
            section.changeUpStation(downStation);
            section.changeDistance(changedDistance);
        }
        getSections().add(index, new Section(this, upStation, downStation, distance));

    }

    public void addSectionWhenDownStationMatch(Station upStation, Station downStation, int distance){
        int index = getStations().indexOf(downStation);
        //앞의 요소 변경
        if(index>0){
            Section section = getSections().get(index-1);
            int changedDistance = section.getDistance()-distance;
            if(changedDistance<=0) throw new InvalidDistanceException("거리가 맞지 않습니다");
            section.changeDownStation(upStation);
            section.changeDistance(changedDistance);
        }
        getSections().add(index, new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Long stationId) {
        if (getSections().size() <= 1) {
            throw new RuntimeException();
        }

        boolean isNotValidUpStation = getStations().get(getStations().size() - 1).getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
