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
import java.util.Optional;
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

    public List<Section> getSections() {
        if(sections.size() < 1){
            throw new NoSuchElementException("저장 된 section정보가 없습니다.");
        }
        List<Section> sectionsSortList = new ArrayList<>();
        Section firstSection = getFirstSection();
        System.out.println("first" );
        System.out.println(firstSection.toString());
        sectionsSortList.add(firstSection);
        for(int i = 0; i < sections.size(); i++ ){
            Section nextSection = getNextSection(sectionsSortList.get(i));
            if(Objects.isNull(nextSection)){
                break;
            }
            sectionsSortList.add(nextSection);
        }
        return sectionsSortList;
    }

    public void add(Section newSection) {
        if(sections.size() > 0){
            conditionCheckAndModify(newSection);
        }

        this.sections.add(newSection);
    }

    public void conditionCheckAndModify(Section newSection){

        if (!Objects.isNull(getBetweenSection(newSection))){
            addBetweenSection(newSection, getBetweenSection(newSection));
            return;
        }
        System.out.println("not Between Section Add");
    }

    public Section getBetweenSection(Section newSection){
        System.out.println(newSection.toString());
        return sections.stream().filter(
                section -> newSection.getUpStation().equals(section.getUpStation())
                        || newSection.getDownStation().equals(section.getDownStation())
        ).findFirst().orElse(null);
    }

    public void addBetweenSection(Section newSection, Section matchedSection){
//        역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        System.out.println("between Section Add");
        if( newSection.getDistance() >= matchedSection.getDistance()){
            throw new BadRequestException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
        matchedSection.minusDistance(newSection.getDistance());
        matchedSection.modifyStation(newSection);
        System.out.println("종료");

    }

    public List<Station> getStations(){
        return getSections().stream()
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
        return sections.stream()
                .filter(this::isLastSection)
                .findFirst().orElseThrow(NoSuchElementException::new);
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

    public Section getFirstSection(){
        if(sections.size() < 1){
            throw new NoSuchElementException("저장 된 section정보가 없습니다.");
        }
        return sections.stream()
                .filter(this::isFirstSection)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

    }

    public Section getNextSection(Section section){

        return sections.stream()
                .filter(thisSection -> section.getDownStation().equals(thisSection.getUpStation()))
                .findFirst()
                .orElse(null);

    }
    public Boolean isFirstSection(Section section){
        return sections.stream()
                .noneMatch(thisSection -> section.getUpStation().getId().equals(thisSection.getDownStation().getId()));
    }

    public Boolean isLastSection(Section section){
        return sections.stream()
                .noneMatch(thisSection -> section.getDownStation().getId().equals(thisSection.getUpStation().getId()));
    }



}