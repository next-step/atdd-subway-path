package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.constants.ErrorMessage;
import nextstep.subway.applicaion.exception.SectionDeleteException;
import nextstep.subway.applicaion.exception.SectionExistException;
import nextstep.subway.applicaion.exception.SectionNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;


@Embeddable
@NoArgsConstructor
@Getter
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }
    public void add(Section section) {
        this.sections.add(section);
    }
    public int size(){
        return this.sections.size();
    }

    public Section get(int index) {
        return this.sections.get(index);
    }

    public void addSection(Section section) {
        if (isExistedStation(section.getUpStation()) && isExistedStation(section.getDownStation())) {
            throw new SectionExistException(ErrorMessage.SECTION_ALREADY_EXISTED);
        }
        if (!isExistedStation(section.getUpStation()) && !isExistedStation(section.getDownStation())) {
            throw new SectionExistException(ErrorMessage.CONCLUED_UP_OR_DOWN);
        }
        modifyExistedSection(section);
        sections.add(section);
    }

    private void modifyExistedSection(Section addSection) {
        if (isExistedUpStation(addSection.getUpStation())) {
            Section section = findSectionByUpStation(addSection.getUpStation());
            section.modifyDistanceForAdd(addSection.getDistance());
            section.modifyUp(addSection.getDownStation());
        }
        if (isExistedDownStation(addSection.getDownStation())) {
            Section section = findSectionByDownStation(addSection.getDownStation());
            section.modifyDistanceForAdd(addSection.getDistance());
            section.modifyDown(addSection.getUpStation());
        }
    }

    public void deleteSection(Station station) {
        if(sections.size() == 1) {
            throw new SectionDeleteException(ErrorMessage.SECTION_MORE_THAN_TWO);
        }
        if(!isExistedStation(station)) {
            throw new SectionDeleteException(ErrorMessage.STATION_IS_NOT_SELECTED);
        }

        boolean isUp = isExistedUpStation(station);
        boolean isDown = isExistedDownStation(station);

        // up
        if(isUp && !isDown) {
            sections.remove(findSectionByDownStation(station));
        }
        //down
        if(!isUp && isDown){
            sections.remove(findSectionByUpStation(station));
        }
        // middle
        if(isUp && isDown) {
            Section up = findSectionByUpStation(station);
            Section down = findSectionByDownStation(station);
            up.modifyUp(down.getUpStation());
            up.modifyDistanceForRemove(down.getDistance());
            sections.remove(down);
        }

    }

    public boolean isExistedStation(Station station) {
        return sections.stream().anyMatch(section -> section.isExistedStation(station));
    }

    public boolean isExistedUpAndDownStation(Station up, Station down) {
        return sections.stream().anyMatch(section -> section.isUp(up) && section.isDown(down));
    }
    public boolean isExistedUpStation(Station station) {
        return sections.stream().anyMatch(section -> section.isUp(station));
    }

    public boolean isExistedDownStation(Station station) {
        return sections.stream().anyMatch(section -> section.isDown(station));
    }

    public Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isUp(station))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("구간을 찾을 수 없습니다."));
    }
    public Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isDown(station))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("구간을 찾을 수 없습니다."));
    }

    public Section findSectionByUpAndDownStation(Station up, Station down) {
        return sections.stream()
                .filter(section -> section.isUp(up) && section.isDown(down))
                .findAny()
                .orElseThrow(() -> new SectionNotFoundException("구간을 찾을 수 없습니다."));
    }

    public boolean empty() {
        return sections.isEmpty();
    }

}
