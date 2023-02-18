package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.domain.VO.SectionsVO;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }
        lengthValidateCheck(section);
        existValidateCheck(section);
        notExistsValidateCheck(section);
        int index = -1;
        Section foundSection = null;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(section.getUpStation())) {
                index = i;
                foundSection = sections.get(i);
                break;
            }
        }
        if (foundSection != null) {
            splitAndSaveSections(index, section.getDownStation(), section.getDistance(), foundSection);
            return;
        }
        sections.add(section);
    }

    public void removeSection(Station station) {
        removeSectionValidate(station);
        sections.remove(sections.size() - 1);
    }

    public SectionsVO sort() {
        Map<Station, Integer> stationCount = countStations();
        Station firstStation = findStartStation(stationCount);
        SectionsVO sectionsVO = makeSortedSections(firstStation);
        return sectionsVO;
    }

    private void notExistsValidateCheck(Section newSection) {
        int existsCount = 0;
        for(Section oldSection : sections) {
            if(oldSection.getUpStation().equals(newSection.getUpStation()) || oldSection.getDownStation().equals(newSection.getUpStation())) {
                existsCount++;
            }
            if(oldSection.getUpStation().equals(newSection.getDownStation()) || oldSection.getDownStation().equals(newSection.getDownStation())) {
                existsCount++;
            }
        }
        if(existsCount != 1) {
            throw new InvalidValueException(ErrorCode.NOT_EXISTS_STATIONS_OF_NEW_SECTION);
        }
    }

    private void existValidateCheck(Section newSection) {
        int duplicateCount = 0;
        for(Section oldSection : sections) {
            if(oldSection.getUpStation().equals(newSection.getUpStation()) || oldSection.getDownStation().equals(newSection.getUpStation())) {
                duplicateCount++;
            }
            if(oldSection.getUpStation().equals(newSection.getDownStation()) || oldSection.getDownStation().equals(newSection.getDownStation())) {
                duplicateCount++;
            }
        }
        if(duplicateCount == 2) {
            throw new InvalidValueException(ErrorCode.ALREADY_EXISTED_STATIONS_OF_NEW_SECTION);
        }
    }

    private void lengthValidateCheck(Section newSection) {
        for (Section oldSection : sections) {
            if (oldSection.getUpStation().equals(newSection.getUpStation())) {
                if (oldSection.getDistance() <= newSection.getDistance()) {
                    throw new InvalidValueException(ErrorCode.NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH);
                }
            }
        }
    }

    private void removeSectionValidate(Station station) {
        if (!(sections.get(sections.size() - 1).getUpStation().equals(station) || sections.get(sections.size() - 1).getDownStation().equals(station))) {
            throw new InvalidValueException(ErrorCode.NOT_STATION_OF_END_SECTION);
        }
    }

    private void splitAndSaveSections(final int index, Station newDownStation, int newSectionDistance, Section oldSection) {
        Station oldUpStation = oldSection.getUpStation();
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();
        sections.remove(oldSection);
        sections.add(index, new Section(oldSection.getLine(), oldUpStation, newDownStation, newSectionDistance));
        sections.add(index + 1, new Section(oldSection.getLine(), newDownStation, oldDownStation, oldDistance - newSectionDistance));
    }

    private SectionsVO makeSortedSections(Station firstStation) {
        boolean result = false;
        SectionsVO sectionsVO = new SectionsVO();
        while (!result) {
            for (Section section : sections) {
                if (section.getUpStation().equals(firstStation)) {
                    firstStation = section.getDownStation();
                    sectionsVO.add(section);
                    break;
                }
                if (sectionsVO.getSections().size() == sections.size()) {
                    result = true;
                    break;
                }
            }
        }
        return sectionsVO;
    }

    private Station findStartStation(Map<Station, Integer> stationCount) {
        List<Station> stations = new ArrayList<>();
        for (Map.Entry<Station, Integer> entry : stationCount.entrySet()) {
            if (entry.getValue() == 1) {
                stations.add(entry.getKey());
            }
        }
        Station firstStation = null;
        for (Station station : stations) {
            for (Section section : sections) {
                if (section.getUpStation().equals(station)) {
                    firstStation = station;
                }
            }
        }
        return firstStation;
    }

    private Map<Station, Integer> countStations() {
        Map<Station, Integer> stationCount = new HashMap<>();
        for (Section section : sections) {
            stationCount.put(section.getUpStation(), stationCount.getOrDefault(section.getUpStation(), 0) + 1);
            stationCount.put(section.getDownStation(), stationCount.getOrDefault(section.getDownStation(), 0) + 1);
        }
        return stationCount;
    }
}
