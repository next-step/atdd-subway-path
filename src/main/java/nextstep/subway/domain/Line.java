package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station firstUpStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station lastDownStation;

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

    public void addSection(Section newSection){

        //노선의 첫 구간추가일 경우
        if(this.sections.isEmpty()){
            this.firstUpStation = newSection.getUpStation();
            this.lastDownStation = newSection.getDownStation();

            sections.add(newSection);
            return;
        }

        List<Station> orderedStationList = this.getOrderedStationList();
        boolean isUpStationAlreadyRegistered = orderedStationList.contains(newSection.getUpStation());
        boolean isDownStationAlreadyRegistered = orderedStationList.contains(newSection.getDownStation());

        if(isUpStationAlreadyRegistered && isDownStationAlreadyRegistered){
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if( !(isUpStationAlreadyRegistered) && !(isDownStationAlreadyRegistered)){
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }

        //새로운 역을 상행 종점으로 등록할 경우
        if(this.firstUpStation.getId()==newSection.getDownStation().getId()){
            this.firstUpStation = newSection.getUpStation();

            sections.add(newSection);
            return;
        }

        //새로운 역을 하행 종점으로 등록할 경우
        if(this.lastDownStation.getId()==newSection.getUpStation().getId()){
            this.lastDownStation = newSection.getDownStation();

            sections.add(newSection);
            return;
        }


        //역 사이에 새로운 역을 등록할 경우

        ////새로운 역의 상행역이 기존역인 경우
        if(isUpStationAlreadyRegistered){
            Section oldSection = findSectionByUpStation(newSection.getUpStation().getId()).get();

            if(newSection.getDistance()>=oldSection.getDistance()){
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            sections.add(new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
            sections.remove(oldSection);
            sections.add(newSection);
            return;

        }
        ////새로운 역의 하행역이 기존역인 경우
        if(isDownStationAlreadyRegistered){
            Section oldSection = findSectionByDownStation(newSection.getDownStation().getId()).get();

            if(newSection.getDistance()>=oldSection.getDistance()){
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            sections.add(new Section(newSection.getLine(), oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance()));
            sections.remove(oldSection);
            sections.add(newSection);
            return;
        }



    }



    public List<Station> getOrderedStationList(){
        if(this.sections.isEmpty()){
            return Collections.emptyList();
        }
        else{
            List<Station> stations = this.getOrderdSectionList().stream()
                    .map(Section::getDownStation)
                    .collect(Collectors.toList());

            stations.add(0, this.firstUpStation);

            return stations;
        }
    }

    public List<Section> getOrderdSectionList(){
        List<Section> orderedSections = new ArrayList<>();

        Section nextSection = findSectionByUpStation(firstUpStation.getId()).get();
        orderedSections.add(nextSection);

        while(findSectionByUpStation(nextSection.getDownStation().getId()).isPresent()) {
            nextSection = findSectionByUpStation(nextSection.getDownStation().getId()).get();
            orderedSections.add(nextSection);
        }

        return List.copyOf(orderedSections);
    }

    private Optional<Section> findSectionByUpStation(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId()==upStationId)
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId()==downStationId)
                .findFirst();
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
