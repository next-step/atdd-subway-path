package nextstep.subway.domain;

import nextstep.subway.applicaion.exceptions.DataNotFoundException;
import nextstep.subway.applicaion.exceptions.InvalidStationParameterException;
import nextstep.subway.applicaion.exceptions.SectionNotEnoughException;
import nextstep.subway.enums.exceptions.ErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section newSection) {
        if (!this.sections.isEmpty()) {
            Section alreadyRegisterSection = getBetweenSection(newSection);
            if (Objects.nonNull(alreadyRegisterSection)) {
                alreadyRegisterSection.isSameDistance(newSection.getDistance());
                alreadyRegisterSection.modifyBetweenSection(newSection);
                alreadyRegisterSection.minusDistance(newSection.getDistance());
            }
        }
        sections.add(newSection);
    }

    private Section getBetweenSection(Section newSection) {
        return this.sections.stream()
                .filter(section -> section.isSameUpDownStation(newSection))
                .findFirst()
                .orElse(null);
    }

    public List<Station> getStation() {
        return sections.stream()
                .map(Section::getRelatedStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        List<Section> sectionsSortList = new ArrayList<>();
        if(sections.isEmpty()){
            return Collections.emptyList();
        }
        Section firstSection = getFirstSection();
        sectionsSortList.add(firstSection);

//        sectionsSortList.addAll(sectionsSortList.stream().takeWhile(section -> Objects.nonNull(getNextSection(section))).collect(Collectors.toList()));

        for(int i = 0; i < sections.size(); i++ ){
            Section nextSection = getNextSection(sectionsSortList.get(i));
            if(Objects.isNull(nextSection)){
                break;
            }
            sectionsSortList.add(nextSection);
        }
        return sectionsSortList;
    }

    public Section getFirstSection(){
        if(sections.isEmpty()){
            throw new DataNotFoundException(ErrorCode.NOT_ENOUGH_SECTION);
        }
        return sections.stream()
                .filter(section -> isFirstSection(section))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.NOT_ENOUGH_SECTION));
    }

    public Boolean isFirstSection(Section section){
        return sections.stream()
                .noneMatch(currentSection -> section.getUpStation().equals(currentSection.getDownStation()));
    }

    public Section getNextSection(Section section){
        return sections.stream()
                .filter(thisSection -> section.getDownStation().equals(thisSection.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    public Station getLastStation() {
        if (sections.isEmpty())
            throw new DataNotFoundException(ErrorCode.NOT_FOUND_SECTION);

        return sections.get(sections.size() - 1).getDownStation();
    }

    public Section getLastSection(){
        if(sections.size() < 1){
            throw new NoSuchElementException("저장 된 section정보가 없습니다.");
        }

        return sections.get(sections.size() - 1);
    }

    public void removeSection(Station lastStation) {
        if (!lastStation.isSame(getLastStation()))
            throw new InvalidStationParameterException(ErrorCode.IS_NOT_SAME_LAST_STATION);
        sections.remove(getLastSection());
    }
}
