package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.domain.VO.SectionsVO;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class Sections {
    private static final String ADD_IN_THE_MIDDLE = "addInTheMiddle";
    private static final String ADD_AT_THE_BEGINNING = "addAtTheBeginning";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return;
        }
        lengthValidateCheck(section);
        existsValidateCheck(section);
        notExistsValidateCheck(section);
        int index = -1;
        Section foundSection = null;
        String status = null;
        for (int i = 0; i < sections.size(); i++) {
            if (addableFirstSection(section)) {
                status = ADD_AT_THE_BEGINNING;
                break;
            }
            if (equalsUpStationOfSections(i, section)) {
                index = i;
                foundSection = sections.get(i);
                status = ADD_IN_THE_MIDDLE;
                break;
            }
        }
        if (ADD_AT_THE_BEGINNING.equals(status)) {
            sections.add(0, section);
            return;
        }
        if (foundSection != null && ADD_IN_THE_MIDDLE.equals(status)) {
            splitAndSaveSections(index, section.getDownStation(), section.getDistance(), foundSection);
            return;
        }
        sections.add(section);
    }

    public void removeSection(Station station) {
        if (removableFirstStation(station)) {
            sections.remove(0);
            return;
        }
        if (removableLastStation(station)) {
            sections.remove(sections.size() - 1);
            return;
        }
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(station)) {
                Line line = sections.get(i).getLine();
                Station newUpStation = sections.get(i - 1).getUpStation();
                Station newDownStation = sections.get(i).getDownStation();
                int priorDistance = sections.get(i - 1).getDistance();
                int nextDistance = sections.get(i).getDistance();
                sections.remove(i - 1);
                sections.remove(i - 1);
                sections.add(i - 1, new Section(line, newUpStation, newDownStation, priorDistance + nextDistance));
                return;
            }
        }
    }

    public List<Station> getSortedStations() {
        SectionsVO sortedSections = getSortedSections();
        List<Station> stations = new ArrayList<>();
        stations.add(sortedSections.findFirstUpStation());
        stations.addAll(sortedSections.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
        return stations;
    }

    public boolean hasSection() {
        return !sections.isEmpty();
    }

    private boolean addableFirstSection(Section section) {
        return sections.get(0).getUpStation().equals(section.getDownStation());
    }

    private boolean removableFirstStation(Station station) {
        return sections.get(0).getUpStation().equals(station);
    }

    private boolean removableLastStation(Station station) {
        return sections.get(sections.size() - 1).getDownStation().equals(station);
    }

    private boolean equalsUpStationOfSections(int i, Section section) {
        return sections.get(i).getUpStation().equals(section.getUpStation());
    }

    private void notExistsValidateCheck(Section newSection) {
        int existsCount = 0;
        for (Section oldSection : sections) {
            if (oldSection.containsUpStation(newSection)) {
                existsCount++;
            }
            if (oldSection.containsDownStation(newSection)) {
                existsCount++;
            }
        }
        if (existsCount != 1) {
            throw new InvalidValueException(ErrorCode.NOT_EXISTS_STATIONS_OF_SECTION);
        }
    }

    private void existsValidateCheck(Section newSection) {
        int duplicateCount = 0;
        for (Section oldSection : sections) {
            if (oldSection.containsUpStation(newSection)) {
                duplicateCount++;
                continue;
            }
            if (oldSection.containsDownStation(newSection)) {
                duplicateCount++;
            }
        }
        if (duplicateCount == 2) {
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

    private SectionsVO getSortedSections() {
        SectionsVO sectionsVO = new SectionsVO();
        Collections.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section s1, Section s2) {
                if (s1.getDownStation().equals(s2.getUpStation())) {
                    return -1;
                }
                return 0;
            }
        });
        sections
                .forEach(sectionsVO::add);
        return sectionsVO;
    }
}
