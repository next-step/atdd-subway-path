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
        SectionRemoveChecker stationRemoveChecker = new SectionRemoveChecker(sections, targetStation);

        if (stationRemoveChecker.isNotFound()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        if (stationRemoveChecker.isUpLastDelete()) {
            checkLastSection();
            sections.remove(stationRemoveChecker.getUpwardIncludeSection());
            return;
        }

        if (stationRemoveChecker.isDownLastDelete()) {
            checkLastSection();
            sections.remove(stationRemoveChecker.getDownwardIncludeSection());
            return;
        }

        Section baseSection = stationRemoveChecker.getDownwardIncludeSection();
        Section deleteSection = stationRemoveChecker.getUpwardIncludeSection();
        baseSection.removeInterStation(deleteSection);
        sections.remove(deleteSection);
    }

    private void checkLastSection() {
        if (sections.size() <= 1) {
            throw new CustomException(ErrorCode.CAN_NOT_REMOVE_STATION);
        }
    }

    public Integer getDistance() {
        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStationsByOrder() {
        List<Station> result = new ArrayList<>();

        Section firstSection = getFirstSection();
        result.add(firstSection.getUpwardStation());
        result.add(firstSection.getDownwardStation());
        Station pivot = getNextStation(firstSection.getDownwardStation());
        while (pivot != null) {
            result.add(pivot);
            pivot = getNextStation(pivot);
        }
        return result;
    }

    private Station getNextStation(Station station) {
        for (Section section : sections) {
            if (section.hasSameUpwardStation(station)) {
                return section.getDownwardStation();
            }
        }
        return null;
    }

    public Station getDownLastStation() {
        if (sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Section anySection = sections.get(0);
        return getDownLastStation(anySection.getDownwardStation());
    }

    public Station getUpLastStation() {
        if (sections.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Section anySection = sections.get(0);
        return getUpLastStation(anySection.getUpwardStation());
    }

    private Section getFirstSection() {
        Station firstStation = getUpLastStation();
        for (Section section : sections) {
            if (section.hasSameUpwardStation(firstStation)) {
                return section;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND);
    }

    private Station getUpLastStation(Station targetStation) {
        for (Section section : sections) {
            if (section.hasSameDownwardStation(targetStation)) {
                targetStation = section.getUpwardStation();
                getUpLastStation(targetStation);
            }
        }
        return targetStation;
    }

    private Station getDownLastStation(Station targetStation) {
        for (Section section : sections) {
            if (section.hasSameUpwardStation(targetStation)) {
                targetStation = section.getDownwardStation();
                getDownLastStation(targetStation);
            }
        }
        return targetStation;
    }
}
