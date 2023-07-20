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

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section newSection) {
        checkValidStations(newSection.getUpwardStation(), newSection.getDownwardStation());
        addNewSection(newSection);
    }

    private void addNewSection(Section newSection) {
        for (Section section : sections) {
            if (section.hasSameUpwardStation(newSection.getUpwardStation())) {
                section.insertDownwardInterStation(newSection);
                sections.add(newSection);
                return;
            }

            if (section.hasSameDownwardStation(newSection.getDownwardStation())) {
                section.insertUpwardInterStation(newSection);
                sections.add(newSection);
                return;
            }
        }
        sections.add(newSection);
    }

    private void checkValidStations(Station upStation, Station downStation) {
        if (sections.isEmpty()) {
            return;
        }

        boolean hasUpwardStation = false;
        boolean hasDownwardStation = false;

        for (Section section : sections) {
            if (section.checkStationInSection(upStation)) {
                hasUpwardStation = true;
            }

            if (section.checkStationInSection(downStation)) {
                hasDownwardStation = true;
            }
        }

        if (isAlReadyAllInStation(hasUpwardStation, hasDownwardStation)) {
            throw new CustomException(ErrorCode.ALREADY_IN_LINE);
        }
        if (isAllStationNotIn(hasUpwardStation, hasDownwardStation)) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }

    private boolean isAlReadyAllInStation(boolean hasUpwardStation, boolean hasDownwardStation) {
        return hasDownwardStation == hasUpwardStation && hasUpwardStation;
    }

    private boolean isAllStationNotIn(boolean hasUpwardStation, boolean hasDownwardStation) {
        return hasDownwardStation == hasUpwardStation && !hasUpwardStation;
    }

    public void removeSection(Station targetStation){

        if (sections.size() <= 1) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        if (!getDownLastStation().equals(targetStation)) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }

        for (Section section : sections) {
            if (section.hasSameDownwardStation(targetStation)) {
                sections.remove(section);
                return;
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

    public Station getDownLastStation() {
        if (sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Station downwardStation = null;
        for (Section section : sections) {
            if (downwardStation == null) {
                downwardStation = section.getDownwardStation();
                continue;
            }

            if (section.hasSameUpwardStation(downwardStation)) {
                downwardStation = section.getDownwardStation();
            }
        }
        return downwardStation;
    }

    public Station getUpLastStation() {
        if (sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Station upwardStation = null;
        for (Section section : sections) {
            if (upwardStation == null) {
                upwardStation = section.getUpwardStation();
                continue;
            }

            if (section.hasSameDownwardStation(upwardStation)) {
                upwardStation = section.getUpwardStation();
            }
        }
        return upwardStation;
    }
}