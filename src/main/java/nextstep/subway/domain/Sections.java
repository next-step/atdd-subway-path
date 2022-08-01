package nextstep.subway.domain;

import nextstep.subway.applicaion.exceptions.DataNotFoundException;
import nextstep.subway.applicaion.exceptions.InvalidStationParameterException;
import nextstep.subway.applicaion.exceptions.SectionNotEnoughException;
import nextstep.subway.enums.exceptions.ErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section newSection) {
        if (this.sections.isEmpty()) {
            throw new SectionNotEnoughException(ErrorCode.NOT_ENOUGH_SECTION);
        }

        Section alreadyRegisterSection = getBetweenSection(newSection);
        alreadyRegisterSection.modifyBetweenSection(newSection);
        alreadyRegisterSection.minusDistance(newSection.getDistance());
        sections.add(newSection);
    }

    private Section getBetweenSection(Section newSection) {
        return this.sections.stream()
                .filter(section -> section.isSameUpDownStation(newSection))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.NOT_FOUND_STATION));
    }

    public List<Station> getStation() {
        return sections.stream()
                .map(Section::getRelatedStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
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
