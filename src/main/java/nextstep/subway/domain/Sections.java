package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        addValidation(newSection);
        addByPosition(newSection);
    }

    private void addByPosition(Section newSection) {
        if (sections.isEmpty() || isEndSection(newSection)) {
            sections.add(newSection);
            return;
        }
        addMiddle(newSection);
    }

    private void addValidation(Section newSection) {
        for (Section section : sections) {
            notMatchAnyStation(newSection, section);
            isAlreadyExistSection(newSection, section);
            isDistanceIssueSection(newSection, section);
        }
    }

    private void notMatchAnyStation(Section newSection, Section section) {
        List<Station> stationList = Arrays.asList(section.getDownStation(), section.getUpStation(),
            newSection.getDownStation(), newSection.getUpStation());
        Set<Station> stationSet = new HashSet<>(stationList);

        if (stationSet.size() == stationList.size()) {
            throw new SectionException("매칭되는 역이 없습니다.");
        }
    }

    private void isDistanceIssueSection(Section newSection, Section section) {
        if (isSectionInMiddle(newSection, section)) {
            if (newSection.getDistance() >= section.getDistance()) {
                throw new SectionException("구간 길이 조정이 필요합니다.");
            }
        }
    }

    private void isAlreadyExistSection(Section newSection, Section section) {

        boolean twoSectionMatchExactly = section.getDownStation() == newSection.getDownStation()
            && section.getUpStation() == newSection.getUpStation();

        boolean twoSectionMatchReversely = section.getDownStation() == newSection.getUpStation()
            && section.getUpStation() == newSection.getDownStation();

        if (twoSectionMatchReversely || twoSectionMatchExactly) {
            throw new SectionException("동일한 구간은 등록할 수 없습니다.");
        }
    }

    private void addMiddle(Section newSection) {
        modifyMatchSection(newSection);
        sections.add(newSection);
    }

    private void modifyMatchSection(Section newSection) {
        Section matchSection = this.sections.stream()
            .filter(section -> isSectionInMiddle(newSection, section))
            .findFirst()
            .orElseThrow(() -> new SectionException("구간이 존재하지 않습니다"));

        if (matchSection.getUpStation() == newSection.getUpStation()) {
            matchSection.setUpStation(newSection.getDownStation());
            matchSection.minusDistance(newSection.getDistance());
            return;
        }
        matchSection.setDownStation(newSection.getUpStation());
        matchSection.minusDistance(newSection.getDistance());
    }

    private static boolean isSectionInMiddle(Section newSection, Section section) {
        return section.getUpStation().equals(newSection.getUpStation())
            || section.getDownStation().equals(newSection.getDownStation());
    }

    private Boolean isEndSection(Section newSection) {
        List<Station> stations = getSortedStations();
        assert (stations.size() >= 2);

        Station headStation = stations.get(0);
        Station tailStation = stations.get(stations.size() - 1);

        return headStation == newSection.getDownStation() || tailStation == newSection.getUpStation();
    }

    public List<Station> getStations() {
        var upStations = this.sections.stream().map(Section::getUpStation);
        var downStations = this.sections.stream().map(Section::getDownStation);

        return Stream.concat(upStations, downStations)
            .distinct()
            .collect(Collectors.toList());
    }

    public void remove(Station station) {
        removeValidate();
        List<Section> sortedSections = getSortedSections();
        List<Section> adjacentSection = getAdjacentSection(station, sortedSections);
        removeSection(adjacentSection);
    }

    private void removeSection(List<Section> adjacentSection) {
        if (adjacentSection.size() == 1) {
            removeEndSection(adjacentSection);
        } else {
            removeMiddleSection(adjacentSection);
        }
    }

    private void removeEndSection(List<Section> adjacentSection) {
        Section section = adjacentSection.get(0);
        this.sections.remove(section);
    }

    private void removeMiddleSection(List<Section> adjacentSection) {
        Section firstSection = adjacentSection.get(0);
        Section secondSection = adjacentSection.get(1);

        firstSection.setDownStation(secondSection.getDownStation());
        firstSection.plusDistance(secondSection.getDistance());
        this.sections.remove(secondSection);
    }

    private List<Section> getAdjacentSection(Station station, List<Section> sortedSections) {
        return sortedSections.stream()
            .filter(s -> s.getUpStation() == station || s.getDownStation() == station)
            .collect(Collectors.toList());
    }

    private void removeValidate() {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간이 존재하지 않습니다.");
        }
        if (sections.size() == 1) {
            throw new SectionException("구간을 삭제할 수 없습니다.");
        }
    }

    private List<Section> getSortedSections() {
        List<Section> result = new ArrayList<>();
        Station nextStation = getHeadStation().get(0);

        while (sections.size() != result.size()) {
            for (Section section : sections) {
                if (section.getUpStation() == nextStation) {
                    result.add(section);
                    nextStation = section.getDownStation();
                }
            }
        }
        return result;
    }

    public List<Station> getSortedStations() {
        List<Station> stations = getHeadStation();

        if (stations.isEmpty()) {
            return Collections.emptyList();
        }

        Station nextStation = stations.get(0);

        while (stations.size() <= sections.size()) {
            for (Section section : sections) {
                if (section.getUpStation() == nextStation) {
                    nextStation = section.getDownStation();
                    stations.add(nextStation);
                }
            }
        }
        return stations;
    }

    private List<Station> getHeadStation() {
        List<Station> upStations = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        upStations.removeAll(downStations);
        return upStations;
    }
}
