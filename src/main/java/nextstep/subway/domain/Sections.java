package nextstep.subway.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();



    public void addSection(Line line , Section newSection){

        //노선의 첫 구간추가일 경우
        if(this.sectionList.isEmpty()){
            line.updateFirstUpStation(newSection.getUpStation());
            line.updateLastDownStation(newSection.getDownStation());
            sectionList.add(newSection);
            return;
        }

        List<Station> orderedStationList = this.getOrderedStationList(line);
        boolean isUpStationAlreadyRegistered = orderedStationList.contains(newSection.getUpStation());
        boolean isDownStationAlreadyRegistered = orderedStationList.contains(newSection.getDownStation());

        if(isUpStationAlreadyRegistered && isDownStationAlreadyRegistered){
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if( !(isUpStationAlreadyRegistered) && !(isDownStationAlreadyRegistered)){
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }

        //새로운 역을 상행 종점으로 등록할 경우
        if(line.getFirstUpStation().getId()==newSection.getDownStation().getId()){
            line.updateFirstUpStation(newSection.getUpStation());
            sectionList.add(newSection);
            return;
        }

        //새로운 역을 하행 종점으로 등록할 경우
        if(line.getLastDownStation().getId()==newSection.getUpStation().getId()){
            line.updateLastDownStation(newSection.getDownStation());
            sectionList.add(newSection);
            return;
        }


        //역 사이에 새로운 역을 등록할 경우

        ////새로운 역의 상행역이 기존역인 경우
        if(isUpStationAlreadyRegistered){
            Section oldSection = findSectionByUpStation(newSection.getUpStation().getId()).get();

            if(newSection.getDistance()>=oldSection.getDistance()){
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            sectionList.add(new Section(newSection.getLine(), newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
            sectionList.remove(oldSection);
            sectionList.add(newSection);
            return;

        }
        ////새로운 역의 하행역이 기존역인 경우
        if(isDownStationAlreadyRegistered){
            Section oldSection = findSectionByDownStation(newSection.getDownStation().getId()).get();

            if(newSection.getDistance()>=oldSection.getDistance()){
                throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
            }

            sectionList.add(new Section(newSection.getLine(), oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance()));
            sectionList.remove(oldSection);
            sectionList.add(newSection);
            return;
        }
    }



    public List<Station> getOrderedStationList(Line line){
        if(this.sectionList.isEmpty()){
            return Collections.emptyList();
        }
        else{
            List<Station> stations = this.getOrderdSectionList(line).stream()
                    .map(Section::getDownStation)
                    .collect(Collectors.toList());

            stations.add(0, line.getFirstUpStation());

            return stations;
        }
    }

    public List<Section> getOrderdSectionList(Line line){
        List<Section> orderedSections = new ArrayList<>();

        Section nextSection = findSectionByUpStation(line.getFirstUpStation().getId()).get();
        orderedSections.add(nextSection);

        while(findSectionByUpStation(nextSection.getDownStation().getId()).isPresent()) {
            nextSection = findSectionByUpStation(nextSection.getDownStation().getId()).get();
            orderedSections.add(nextSection);
        }

        return List.copyOf(orderedSections);
    }

    private Optional<Section> findSectionByUpStation(Long upStationId) {
        return this.sectionList.stream()
                .filter(section -> section.getUpStation().getId()==upStationId)
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Long downStationId) {
        return this.sectionList.stream()
                .filter(section -> section.getDownStation().getId()==downStationId)
                .findFirst();
    }

    public void removeSection(Station station){
        validateDeleteSectionRequest(station);
        sectionList.remove(sectionList.size() - 1);
    }

    public void validateDeleteSectionRequest(Station station){

        List<Section> sections = this.sectionList;

        if (!sections.get(sections.size()-1).getUpStation().getId().equals(station.getId())){
            throw new IllegalArgumentException("지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있음");
        }
        if(sections.size()<=1){
            throw new IllegalArgumentException("구간이 1개인 경우 역을 삭제할 수 없음");
        }

    }



}
