package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection){
        if(isAllStationExist(newSection.getUpStation(), newSection.getDownStation())){
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 이미 노선에 포함되어 있습니다.");
        }

        if(isAllStationNotExist(newSection.getUpStation(), newSection.getDownStation())){
            throw new IllegalArgumentException("추가하려는 구간의 상/하행역이 노선에 포함되어 있지 않습니다.");
        }

        sections.add(newSection);
    }

    public void deleteSectionByIndex(Integer index){
        sections.remove(index);
    }

    public Integer getSectionSize(){
        return sections.size();
    }

    public List<Section> getSections(){
        return sections;
    }

    private List<Station> getStations(){
        var upStations = sections.stream().map(Section::getUpStation);
        var downStations = sections.stream().map(Section::getDownStation);

        return Stream.concat(upStations, downStations).distinct().collect(Collectors.toList());
    }
    private boolean isAllStationExist(Station upStation, Station downStation){
        List<Station> stations = getStations();

        if(stations.contains(upStation) && stations.contains(downStation)){
            return true;
        }

        return false;
    }

    private boolean isAllStationNotExist(Station upStation, Station downStation){
        List<Station> stations = getStations();

        if(!stations.contains(upStation) && !stations.contains(downStation)){
            return true;
        }

        return false;
    }
}