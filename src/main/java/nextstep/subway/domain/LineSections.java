package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.BadRequestSectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class LineSections {

    private static final int MIN_LIMIT = 1;
    @OneToMany(mappedBy = "line" , cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private LineSections(Section section) {
        sections.add(section);
    }

    public static LineSections init(Section section) {
        return new LineSections(section);
    }
    public List<Section> add(Section section){
        isAddableSections(section);
        sections.add(section);
        return sections;
    }

    public void remove(Station station){
        if (sections.size() <= MIN_LIMIT) {
            throw new BadRequestSectionsException("노선에는 하나 이상의 구간이 존재해야합니다.");
        }
        if (!getLastSections().hasDownStation(station)) {
            throw new BadRequestSectionsException("해당 노선의 마지막 하행 종점역이 아닙니다.");
        }

        sections.remove(sections.size() - 1);
    }


    public List<Station> getStations() {
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(getLastSections().getDownStation());
        return stations;
    }

    private Section getLastSections() {
        return sections.get(sections.size()- 1);
    }


    private void isAddableSections(Section newSection) {
        if(sections.isEmpty()) {
            return;
        }
        Station lastStation = sections.get(sections.size() - 1).getDownStation();
        if(!newSection.getUpStation().equals(lastStation)){
            throw new BadRequestSectionsException("구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야합니다.");
        }
        if(sections.stream().anyMatch(section->section.getDownStation().equals(newSection))){
            throw new BadRequestSectionsException("이미 해당 노선에 등록되어있는 역입니다.");
        }
    }

}