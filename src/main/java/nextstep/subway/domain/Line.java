package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStationList(){
        if(this.getSections().isEmpty()){
            return Collections.emptyList();
        }
        else{
            List<Station> stations = this.getSections().stream()
                    .map(Section::getDownStation)
                    .collect(Collectors.toList());

            stations.add(0, this.getSections().get(0).getUpStation());

            return stations;
        }

    }

    public void validateCreateSectionRequest(Long newUpStationId,Long newDownStationId){

        List<Station> stationList = this.getStationList();

        //기존에 등록된 구간이 없을 경우
        if(stationList.isEmpty()){
            return;
        }
        Station lastStation = stationList.get(stationList.size()-1);
        if(!Objects.equals(newUpStationId, lastStation.getId())){
            throw new IllegalArgumentException("새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역과 다릅니다");
        }

        boolean isNewDownStationIdAlreadyExists = stationList.stream()
                .anyMatch(station-> station.getId().equals(newDownStationId));

        if(isNewDownStationIdAlreadyExists){
            throw new IllegalArgumentException("새로운 구간의 하행역이 해당 노선에 이미 등록되어 있습니다.");
        }

    }

    public void validateDeleteSectionRequest(Station station){

        List<Section> sections = this.getSections();

        if (!sections.get(sections.size()-1).getUpStation().getId().equals(station.getId())){
            throw new IllegalArgumentException();
        }
        if(sections.size()<=1){
            throw new IllegalArgumentException();
        }

    }

}
