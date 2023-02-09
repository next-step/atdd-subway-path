package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public void addSection(Section section){
        if (values.isEmpty()) {
            values.add(section); return;
        }
        if (contains(section.getDownStation()) && contains(section.getUpStation())) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }
        if (equalFirstStation(section.getDownStation())){
            values.add(section); return;
        }
        if (equalLastStation(section.getUpStation())) {
            values.add(section); return;
        }
        if (){

        }
        throw new IllegalArgumentException();
    }

    private Section getAfterSection(Section section){
        return values.stream().filter(value -> value.equalUpStation(section.getDownStation())).findFirst().orElseThrow();
    }

    private Section getBeforeSection(Section section){
        return values.stream().filter(value -> value.equalDownStation(section.getUpStation())).findFirst().orElseThrow();
    }

    private Section getIncludedSection(Section section){
        return values.stream().filter(value -> value.equalUpStation(section.getUpStation())).findFirst().orElseThrow();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<Station> getStations() {
        if(values.isEmpty()){
            return Collections.emptyList();
        }

        return values.stream().map(Section::getStations)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    protected boolean equalLastStation(Station station) {
        return values.get(values.size()-1).equalDownStation(station);
    }
    protected boolean equalFirstStation(Station station) {
        return values.get(0).equalUpStation(station);
    }

    public void remove(Station station) {
        values.remove(getStations().indexOf(station)-1);
    }

    public int size(){
        return values.size();
    }

    public boolean contains(Station station){
        return values.stream().anyMatch(section -> section.contain(station));
    }
}
