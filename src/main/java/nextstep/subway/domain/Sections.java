package nextstep.subway.domain;

import nextstep.subway.applicaion.exceptions.DataNotFoundException;
import nextstep.subway.applicaion.exceptions.InvalidSectionParameterException;
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
        if (this.sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        checkExistsAlreadyRegisterUpDownSection(newSection);
        checkExistsNotContainStation(newSection);

        Section alreadyRegisterSection = getBetweenSection(newSection);
        if (Objects.nonNull(alreadyRegisterSection)) {
            alreadyRegisterSection.isSameDistance(newSection.getDistance());
            alreadyRegisterSection.modifyBetweenSection(newSection);
            alreadyRegisterSection.minusDistance(newSection.getDistance());
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
        return getSections().stream()
                .map(Section::getRelatedStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Integer> getStationDistance() {
        return getSections().stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        List<Section> sortSections = new ArrayList<>();
        if(sections.isEmpty()){
            return Collections.emptyList();
        }
        Section firstSection = getFirstSection();
        if (Objects.nonNull(firstSection)) {
            sortSections.add(firstSection);

            for(int i = 0; i < sections.size(); i++ ){
                Section nextSection = getNextSection(sortSections.get(i));
                if(Objects.isNull(nextSection)){
                    break;
                }
                sortSections.add(nextSection);
            }
            return sortSections;
        }
        return sections;
    }

    public Section getFirstSection(){
        if(sections.isEmpty()){
            throw new DataNotFoundException(ErrorCode.NOT_ENOUGH_SECTION);
        }
        return sections.stream()
                .filter(section -> isFirstSection(section))
                .findFirst()
                .orElse(null);
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

    public Station getFirstStation() {
        if (sections.isEmpty())
            throw new DataNotFoundException(ErrorCode.NOT_FOUND_SECTION);

        return sections.get(0).getUpStation();
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

    public void checkExistsAlreadyRegisterUpDownSection(Section newSection) {
        if (checkExistSection(newSection) || checkFirstLastStation(newSection)) {
            throw new InvalidSectionParameterException(ErrorCode.IS_SAME_UP_DOWN_STATION);
        }
    }

    private void checkExistsNotContainStation(Section newSection) {
        if (checkNotContainStation(newSection.getUpStation()) && checkNotContainStation(newSection.getDownStation())) {
            throw new InvalidStationParameterException(ErrorCode.IS_NOT_CONTAIN_STATION);
        }
    }

    private boolean checkNotContainStation(Station newStation) {
        return this.getStation()
                    .stream()
                    .noneMatch(station -> station.isSame(newStation));
    }

    private boolean checkExistSection(Section newSection) {
        return this.sections.stream()
                            .anyMatch(section -> (section.isSameUpStation(newSection.getUpStation()) && section.isSameDownStation(newSection.getDownStation())));
    }

    private boolean checkFirstLastStation(Section newSection) {
        List<Section> sections = getSections();
        return (sections.get(sections.size() - 1).isSameDownStation(newSection.getDownStation()) && sections.get(0).isSameUpStation(newSection.getUpStation()));
    }
}
