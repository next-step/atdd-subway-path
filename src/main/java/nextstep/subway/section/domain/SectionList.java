package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.station.domain.Station;

@Embeddable
public class SectionList {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section newSection) {
        if (isAlreadyInStation(newSection.getDownwardStation())) {
            throw new CustomException(ErrorCode.ALREADY_IN_LINE);
        }

        this.sections.add(newSection);
    }

    public boolean isAlreadyInStation(Station downwardStation) {
        for (Section section : sections) {
            if (section.checkStationInSection(downwardStation)){
                return true;
            }
        }
        return false;
    }

    public Section removeSection(Station targetStation){

        if (sections.size() <= 1) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        for (Section section : sections) {
            if (section.hasSameDownwardStation(targetStation)) {
                sections.remove(section);
                return section;
            }
        }

        throw new CustomException(ErrorCode.NOT_FOUND);
    }

    public Integer getDistance() {
        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }
}
