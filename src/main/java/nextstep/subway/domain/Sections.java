package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.BadRequestException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author a1101466 on 2022/07/13
 * @project subway
 * @description
 */
@Embeddable
@Getter
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("id asc")
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section addSection) {
        this.sections.add(addSection);
    }

    public List<Station> getStations(){
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }
    public void removeSection(Long stationId) {
        Section lastSection = getLastSection();

        if(!Objects.equals(lastSection.getDownStation().getId(), stationId))
            throw new BadRequestException("지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.");

        if(sections.size() < 2)
            throw new BadRequestException("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.");

        sections.remove(lastSection);
    }

    public Section getLastSection(){
        if(sections.size() < 1){
            throw new NoSuchElementException("저장 된 section정보가 없습니다.");
        }
        return sections.get(sections.size()-1);

    }

    public Station getLastStation(){
        return getLastSection().getDownStation();
    }

    public Section getSectionById(Long sectionId){
        return sections.stream()
                .filter(section -> sectionId.equals(section.getId()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

}